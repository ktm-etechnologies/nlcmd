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

import java.util.HashMap;
import java.util.List;

/**
 * Represents a set of commands, with each command being represented by a
 * {@link com.ktm_technologies.nlcmd.MarkovChain}.
 */
public class CommandSet extends HashMap<String, MarkovChain> {

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
        for (CommandSet.Entry<String, MarkovChain> entry : this.entrySet()) {

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

        for (CommandSet.Entry<String, MarkovChain> entry : this.entrySet()) {

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
}
