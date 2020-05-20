/*
 * Copyright 2019 Robert Staudinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ktm_technologies.nlcmd;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.ktm_technologies.nlcmd.Nlcmd.d;
import static com.ktm_technologies.nlcmd.Nlcmd.v;

/**
 * Represents a set of commands, with each command being represented by a
 * {@link MarkovChain}.
 */
@SuppressWarnings("WeakerAccess")
public class CommandSet extends HashMap<Object, MarkovChain> {

    private ScoreMode   _scoreMode;
    private int         _order;
    private Locale      _locale;

    /**
     * Create CommandSet object
     *
     * @param order Order for markov chains created via the {@link CommandSet#put(Object, String[])}
     *              method, that is number of relevant previous steps when matching
     * @param scoreMode See {@link ScoreMode}
     * @param locale Language settings
     * @throws IndexOutOfBoundsException If order < 1
     */
    public CommandSet(int order, ScoreMode scoreMode, Locale locale) {

        if (order < 1) {
            throw new IndexOutOfBoundsException("MarkovChain order size can not be < 1");
        }
        _order = order;
        _scoreMode = scoreMode;
        _locale = locale;
    }

    /**
     * @return Current scoring algorithm, see {@link ScoreMode}
     */
    public ScoreMode getScoreMode() {
        return _scoreMode;
    }

    /**
     * Shortcut for adding markov chains
     *
     * @param key Identifier for this command
     * @param commands Training phrases for the markov chain
     */
    public void put(Object      key,
                    String[]    commands) {

        MarkovChain mc = createChain();
        for (String command : commands) {
            List<String> phrase = Utils.words(command, _locale);
            mc.train(phrase);
        }

        Nlcmd.v(this.getClass(), "put()", key + " : " + mc);
        this.put(key, mc);
    }

    /**
     * Match phrase against all commands and return key for best matching command markov chain.
     *
     * @see MarkovChain#match
     * @param phrase Match phrase
     * @return Key for best matching command or null
     */
    @SuppressLint("DefaultLocale")
    public Object match(List<String> phrase) {

        double maxAvgProbability = 0.0;
        Object key = null;
        double avgProbability;
        for (Entry<Object, MarkovChain> entry : this.entrySet()) {

            MarkovChain mc = entry.getValue();
            avgProbability = mc.match(phrase);
            avgProbability = scoreAndClear(mc, avgProbability);
            if (avgProbability > maxAvgProbability) {
                maxAvgProbability = avgProbability;
                key = entry.getKey();
            }
        }

        d(this.getClass(), ".match()", maxAvgProbability);

        return key;
    }

    /**
     * Scan phrase against all commands and return key for best matching command markov chain.
     *
     * @see MarkovChain#scan
     * @param phrase Match phrase
     * @param matches Map of sub-phrase matches and average probabilities
     * @param placeholders Map of matches placeholders and their actual input
     * @return Key for best matching command or null
     */
    public Object scan(List<String>                     phrase,
                       HashMap<List<String>, Double>    matches,
                       HashMap<String, List<String>>    placeholders) {

        double maxAvgProbability = 0.0;
        Object key = null;
        for (Entry<Object, MarkovChain> entry : this.entrySet()) {

            HashMap<List<String>, Double> matches_ = new HashMap<>();
            HashMap<String, List<String>> placeholders_ = new HashMap<>();
            MarkovChain mc = entry.getValue();
            double avgProbability = mc.scan(phrase, matches_, placeholders_);
            avgProbability = scoreAndClear(mc, avgProbability);
            if (avgProbability > maxAvgProbability) {
                maxAvgProbability = avgProbability;
                key = entry.getKey();

                if (matches != null) {
                    matches.clear();
                    matches.putAll(matches_);
                }

                if (placeholders != null) {
                    placeholders.clear();
                    placeholders.putAll(placeholders_);
                }
            }
        }

        d(this.getClass(), ".match()", maxAvgProbability);

        return key;
    }

    private MarkovChain createChain() {

        MarkovChain mc = new MarkovChain(_order);
        //noinspection StatementWithEmptyBody
        if (_scoreMode == ScoreMode.HIGHEST_AVG) {
            // Nothing to do
        } else  if (_scoreMode == ScoreMode.LONGEST_AVG_REL) {
            Mixin mixin = new Mixin();
            mc.setMixin(mixin);
        } else if (_scoreMode == ScoreMode.LONGEST_AVG_REL_MOR) {
            // TODO implement, fail for now
            return null;
        }

        return mc;
    }

    private double scoreAndClear(MarkovChain mc, double defaultScore) throws RuntimeException {

        if (_scoreMode == ScoreMode.HIGHEST_AVG) {
            return defaultScore;
        } else if (_scoreMode == ScoreMode.LONGEST_AVG_REL) {
            Mixin mixin = mc.getMixin() instanceof Mixin ? (Mixin)mc.getMixin() : null;
            double score = mixin.getScore();
            mixin.clear();
            return score;
        } else if (_scoreMode == ScoreMode.LONGEST_AVG_REL_MOR) {
            throw new RuntimeException("Not implemented");
        } else {
            throw new RuntimeException("Unknown score mode " + _scoreMode);
        }
    }

    /**
     * Custom scoring relative to phrase length
     */
    class Mixin extends MarkovChainMixin {

        private int                             _phraseLen;
        private HashMap<Integer, Accumulator>   _accumulators = new HashMap<>();

        @Override
        int initQuery(List<String> phrase) {

            _phraseLen = phrase.size();
            int id = super.initQuery(phrase);

            v(this.getClass(), ".initQuery() _phraseLen", _phraseLen);

            _accumulators.put(id, new Accumulator());

            return id;
        }

        @Override
        void updateQuery(int id, Node node, Edge edge) {

            Accumulator accumulator = _accumulators.get(id);
            accumulator.add(edge.getProbability());
        }

        @Override
        void finishQuery(int id, boolean success) {

            if (!success) {
                _accumulators.remove(id);
            }
        }

        double getScore() {

            // Find longest sub-match
            int maxLength = 0;
            Accumulator longest = null;
            for (Entry<Integer, Accumulator> entry : _accumulators.entrySet()) {
                if (entry.getValue().getLength() > maxLength) {
                    longest = entry.getValue();
                    maxLength = entry.getValue().getLength();
                }
            }

            if (longest != null) {
                return longest.getProbability() * longest.getLength() / _phraseLen;
            }

            return 0;
        }

        void clear() {
            _phraseLen = 0;
            _accumulators.clear();
        }
    }

    class Accumulator {

        private int     _length;
        private double  _avgP;

        void add(double p) {
            double sum = _avgP * _length;
            sum += p;
            _length++;
            _avgP = sum / _length;
        }

        int getLength() {
            return _length;
        }

        double getProbability() {
            return _avgP;
        }
    }
}
