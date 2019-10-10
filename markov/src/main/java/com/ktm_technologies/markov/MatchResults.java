/*
 * Copyright 2019 KTM Technologies GmbH
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
class MatchResults {

    abstract class Match {

        private final int       _offset;
        private List<String>    _matchPhrase = new LinkedList<>();

        Match(int offset) {

            _offset = offset;
        }

        /**
         * @return Part of phrase that matched
         */
        List<String> getPhrase() { return _matchPhrase; }

        /**
         * @param phrase Phrase represented by this match
         */
        void setPhrase(List<String> phrase) { _matchPhrase = phrase; }

        /**
         * @return Offset of subPhrase in full query phrase
         */
        int getOffset() { return _offset; }

        void append(String word) {
            _matchPhrase.add(word);
        }
    }

    /**
     * Represents a match found against the markov chain.
     */
    class Phrase extends Match {

        private final double            _avgProbability;
        private final Placeholder       _placeholder;

        /**
         * Create Phrase object
         * @param matchPhrase Part of phrase that matched
         * @param offset Offset of matchPhrase in full query phrase
         * @param avgProbability Averaged probability of edges in matchPhrase
         * @param placeholder Phrase matched by placeholder or null
         */
        Phrase(List<String>     matchPhrase,
               int              offset,
               double           avgProbability,
               Placeholder      placeholder) {

            super(offset);

            _avgProbability = avgProbability;
            _placeholder = placeholder;
            this.setPhrase(matchPhrase);
        }

        /**
         * @return Averaged probability of edges in subPhrase
         */
        double getAvgProbability() {
            return _avgProbability;
        }

        /**
         * @return Placeholder match or null
         */
        Placeholder getPlaceholder() { return _placeholder; }
    }

    public class Placeholder extends Match {

        private final String        _keyword;
        private final int           _offset;

        Placeholder(String  keyword,
                    int     offset) {

            super(offset);

            _keyword = keyword;
            _offset = offset;
        }

        void append(String word) {
            super.append(word);
        }

        /**
         * @return Part of phrase that matched
         */
        String getToken() {
            return _keyword;
        }
    }

    private final LinkedList<Phrase>    _entries = new LinkedList<>();
    private Placeholder                 _tmpPlaceholder = null;

    /**
     * Container object for matches
     */
    MatchResults() {}

    /**
     * Add match entry.
     * @param matchPhrase Part of phrase that matched
     * @param offset Offset of matchPhrase in full query phrase
     * @param avgProbability Averaged probability of edges in matchPhrase
     */
    void append(List<String>    matchPhrase,
                int             offset,
                double          avgProbability) {

        _entries.add(new Phrase(matchPhrase, offset, avgProbability, _tmpPlaceholder));
        resetPlaceholder();
    }

    /**
     * @return List of match entries
     */
    LinkedList<Phrase> getEntries() {
        return _entries;
    }

    void createPlaceholder(String keyword, String word, int offset) {

        _tmpPlaceholder = new Placeholder(keyword, offset);
        _tmpPlaceholder.append(word);
    }

    void appendPlaceholder(String word) {

        _tmpPlaceholder.append(word);
    }

    void resetPlaceholder() {

        _tmpPlaceholder = null;
    }
}
