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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Represents a set of commands, with each command being represented by a
 * {@link MarkovChain}.
 */
@SuppressWarnings("WeakerAccess")
public class CommandSet extends HashMap<String, MarkovChain> {

    private int _order;

    /**
     * Create CommandSet object
     *
     * @param order Order for markov chains created via the {@link CommandSet#put(String, String[])}
     *              method, that is number of relevant previous steps when matching
     * @throws ArrayIndexOutOfBoundsException If order < 1
     */
    public CommandSet(int order) throws ArrayIndexOutOfBoundsException {

        if (order < 1) {
            throw new ArrayIndexOutOfBoundsException("MarkovChain order size can not be < 1");
        }
        _order = order;
    }

    /**
     * Shortcut for adding markov chains
     *
     * @param key Identifier for this command
     * @param commands Training phrases for the markov chain
     */
    public void put(String      key,
                    String[]    commands) {

        MarkovChain mc = new MarkovChain(_order);
        for (String command : commands) {
            List<String> phrase = Arrays.asList(command.split(" "));
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
    public String match(List<String> phrase) {

        double maxAvgProbability = -1;
        String key = null;
        for (Entry<String, MarkovChain> entry : this.entrySet()) {

            double avgProbability = entry.getValue().match(phrase);
            if (avgProbability > maxAvgProbability) {
                maxAvgProbability = avgProbability;
                key = entry.getKey();
            }
        }

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
    public String scan(List<String>                     phrase,
                       HashMap<List<String>, Double>    matches,
                       HashMap<String, List<String>>    placeholders) {

        double maxAvgProbability = -1;
        String key = null;

        for (Entry<String, MarkovChain> entry : this.entrySet()) {

            HashMap<List<String>, Double> matches_ = new HashMap<>();
            HashMap<String, List<String>> placeholders_ = new HashMap<>();
            double avgProbability = entry.getValue().scan(phrase, matches_, placeholders_);
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

        return key;
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
        void finishQuery(int id) {
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
