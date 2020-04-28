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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static com.ktm_technologies.nlcmd.Nlcmd.d;
import static com.ktm_technologies.nlcmd.Nlcmd.v;

/**
 * Represents a set of commands, with each command being represented by a
 * {@link MarkovChain}.
 */
@SuppressWarnings("WeakerAccess")
public class CommandSet extends HashMap<Object, MarkovChain> {

    private ScoreMode _scoreMode;
    private int _order;

    /**
     * Create CommandSet object
     *
     * @param order Order for markov chains created via the {@link CommandSet#put(Object, String[])}
     *              method, that is number of relevant previous steps when matching
     * @param scoreMode See {@link ScoreMode}
     * @throws IndexOutOfBoundsException If order < 1
     */
    public CommandSet(int order, ScoreMode scoreMode) {

        if (order < 1) {
            throw new IndexOutOfBoundsException("MarkovChain order size can not be < 1");
        }
        _order = order;
        _scoreMode = scoreMode;
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
            List<String> phrase = Utils.words(command);
            mc.train(phrase);
        }
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

        double maxAvgProbability = -1;
        Object key = null;
        double avgProbability;
        for (Entry<Object, MarkovChain> entry : this.entrySet()) {

            MarkovChain mc = entry.getValue();
            avgProbability = mc.match(phrase);
            avgProbability = extractScore(mc, avgProbability);
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

        double maxAvgProbability = -1;
        Object key = null;
        for (Entry<Object, MarkovChain> entry : this.entrySet()) {

            HashMap<List<String>, Double> matches_ = new HashMap<>();
            HashMap<String, List<String>> placeholders_ = new HashMap<>();
            MarkovChain mc = entry.getValue();
            double avgProbability = mc.scan(phrase, matches_, placeholders_);
            avgProbability = extractScore(mc, avgProbability);
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

    private double extractScore(MarkovChain mc, double defaultScore) throws RuntimeException {

        if (_scoreMode == ScoreMode.HIGHEST_AVG) {
            return defaultScore;
        } else if (_scoreMode == ScoreMode.LONGEST_AVG_REL) {
            Mixin mixin = mc.getMixin() instanceof Mixin ? (Mixin)mc.getMixin() : null;
            return mixin.getScore();
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

        private int             _phraseLen;
        // TODO implement parallel matches per id
        private List<Double>    _sumSubmatchP = new LinkedList<>();
        private List<Integer>   _nSubmatchEdges = new LinkedList<>();

        @Override
        int initQuery(List<String> phrase) {

            _phraseLen = phrase.size();
            int id = super.initQuery(phrase);

            v(this.getClass(), ".initQuery() _phraseLen", _phraseLen);

            // Extend lists
            _sumSubmatchP.add(0.0);
            _nSubmatchEdges.add(0);

            return id;
        }

        @Override
        void updateQuery(int id, Node node, Edge edge) {

            // Update lists
            int length = _sumSubmatchP.size();
            double avgP = _sumSubmatchP.get(length - 1);
            avgP += edge.getProbability();
            _sumSubmatchP.remove(length - 1);
            _sumSubmatchP.add(avgP);

            length = _nSubmatchEdges.size();
            int nEdges = _nSubmatchEdges.get(length - 1);
            nEdges++;
            _nSubmatchEdges.remove(length - 1);
            _nSubmatchEdges.add(nEdges);
        }

        @Override
        void finishQuery(int id, boolean success) {
        }

        double getScore() {

            // Find longest sub-match
            int maxIndex = -1;
            int maxLen = 0;
            ListIterator<Integer> iter = _nSubmatchEdges.listIterator();
            while (iter.hasNext()) {
                int index = iter.nextIndex();
                int len = iter.next();
                if (len > maxLen) {
                    maxLen = len;
                    maxIndex = index;
                }
            }


            if (maxIndex > -1) {
                double avgSubmatchP = _sumSubmatchP.get(maxIndex) / _phraseLen;
                return avgSubmatchP;
            }

            return 0;
        }
    }
}
