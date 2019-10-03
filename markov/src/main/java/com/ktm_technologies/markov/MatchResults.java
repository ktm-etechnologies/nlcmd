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

package com.ktm_technologies.markov;

import java.util.LinkedList;
import java.util.List;

/**
 * Used to capture statistics when querying the markov chain.
 */
public class MatchResults {

    /**
     * Represents a match found against the markov chain.
     */
    public class Entry {

        private final List<String>    _matchPhrase;
        private final int             _offset;
        private final double          _avgProbability;

        /**
         * Create Entry object
         * @param matchPhrase Part of phrase that matched
         * @param offset Offset of matchPhrase in full query phrase
         * @param avgProbability Averaged probability of edges in matchPhrase
         */
        Entry(List<String>  matchPhrase,
              int           offset,
              double        avgProbability) {

            _matchPhrase = matchPhrase;
            _offset = offset;
            _avgProbability = avgProbability;
        }

        /**
         * @return Part of phrase that matched
         */
        public List<String> getMatchPhrase() {
            return _matchPhrase;
        }

        /**
         * @return Offset of subPhrase in full query phrase
         */
        public int getOffset() {
            return _offset;
        }

        /**
         * @return Averaged probability of edges in subPhrase
         */
        public double getAvgProbability() {
            return _avgProbability;
        }
    }

    private final LinkedList<Entry> _entries = new LinkedList<>();

    /**
     * Container object for matches
     */
    public MatchResults() {}

    /**
     * Add match entry.
     * @param matchPhrase Part of phrase that matched
     * @param offset Offset of matchPhrase in full query phrase
     * @param avgProbability Averaged probability of edges in matchPhrase
     */
    void append(List<String> matchPhrase,
                int          offset,
                double       avgProbability) {

        _entries.add(new Entry(matchPhrase, offset, avgProbability));
    }

    /**
     * @return List of match entries
     */
    public LinkedList<Entry> getEntries() {
        return _entries;
    }
}
