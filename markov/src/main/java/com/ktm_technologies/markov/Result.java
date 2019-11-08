package com.ktm_technologies.markov;

import java.util.LinkedList;
import java.util.List;

public class Result {

    /**
     * Abstract baseclass for phrase- and placeholder matches.
     */
    abstract class Match {

        private final int       _offset;
        private List<String> _matchPhrase = new LinkedList<>();

        /**
         * Abstract constructor.
         * @param offset Offset of match
         */
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
         * Create {@link Phrase} object.
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

    /**
     * Placeholder match wrapper.
     */
    public class Placeholder extends Match {

        private final String _token;

        /**
         * Create {@link Placeholder} object.
         * @param token Placeholder word
         * @param offset Offset in current sub-phrase
         */
        Placeholder(String  token,
                    int     offset) {

            super(offset);
            _token = token;
        }

        /**
         * Append {@code word} to the matched content.
         * @param word Input word
         */
        void append(String word) {
            super.append(word);
        }

        /**
         * @return Part of phrase that matched
         */
        String getToken() {
            return _token;
        }
    }

    private final LinkedList<Phrase>    _entries = new LinkedList<>();
    private Placeholder                 _tmpPlaceholder = null;

    /**
     * Container object for matches
     */
    public Result() {}

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

    /**
     * Create new placeholder item associated to currently matched sub-phrase.
     * @param token Placeholder word
     * @param offset Offset of token in current sub-phrase
     */
    void createPlaceholder(String   token,
                           int      offset) {

        _tmpPlaceholder = new Placeholder(token, offset);
    }

    /**
     * Append {}@code word} to current placeholder.
     * @param word Input word
     */
    void appendPlaceholder(String word) {

        _tmpPlaceholder.append(word);
    }

    /**
     * Reset currently accumulated placeholder after failed match.
     */
    void resetPlaceholder() {

        _tmpPlaceholder = null;
    }
}
