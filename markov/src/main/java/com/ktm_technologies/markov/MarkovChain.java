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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents a node label
 */
class Label {

    private final String[] _fragments;

    /**
     * Create node label
     * @param labels Array of name fragments
     */
    Label(String[] labels) {
        _fragments = labels;
    }

    /**
     * @return "Raw" name as string array
     */
    String[] getFragments() {

        return _fragments;
    }

    /**
     * Compare to other label object
     * @param other Other object
     * @return TRUE if matching
     */
    boolean equals(Label other) {

        if (other == null ||
            other.getFragments().length != _fragments.length) {

            return false;
        }

        for (int i = 0; i < _fragments.length; i++) {

            if (!other.getFragments()[i].equals(_fragments[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj != null &&
            obj.getClass() == this.getClass()) {

            Label other = (Label) obj;
            return this.toString().equals(other.toString());
        }

        return false;
    }

    @Override
    public int hashCode() {

        return this.toString().hashCode();
    }

    @NonNull
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < _fragments.length; i++) {
            sb.append(_fragments[i]);
            if (i < _fragments.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}

/**
 * Represents edge to #Node in the chain.
 */
class Edge {

    private final Node  _node;
    private double      _probability;

    /**
     * Create Edge object
     * @param node Target node
     * @param probability Initial probability for the edge
     */
    Edge(Node   node,
         double probability) {

        _node = node;
        _probability = probability;
    }

    /**
     * @return Target node
     */
    Node getNode() {
        return _node;
    }

    /**
     * @return Probability associated to target node
     */
    double getProbability() {
        return _probability;
    }

    /**
     * Weaken probability of edge after new edge has been created on source node.
     * @param nEdgesOld Previous number of edges on source node
     * @param nEdgesNew Updated number of edges on source node
     */
    void weaken(int nEdgesOld,
                int nEdgesNew) {

        _probability = _probability * nEdgesOld / nEdgesNew;
    }

    /**
     * Strengthen probability of edge after new edge has been created on source node.
     * @param nEdgesOld Previous number of edges on source node
     * @param nEdgesNew Updated number of edges on source node
     */
    void strengthen(int nEdgesOld,
                    int nEdgesNew) {

        _probability = (_probability * nEdgesOld + 1) / nEdgesNew;
    }
}

/**
 * Represents a node in the chain.
 */
class Node {

    private final HashMap<Label, Edge>  _edges = new HashMap<>();
    private Label                       _label;

    /**
     * Create Node object.
     * @param label Node label
     */
    Node (Label label) {
        _label = label;
    }

    /**
     * @return Node name
     */
    Label getLabel() {
        return _label;
    }

    /**
     * @return Edges associated to the node
     */
    HashMap<Label, Edge> getEdges() {
        return _edges;
    }

    /**
     * Add new edge to node.
     * @param edge Edge object
     */
    void addEdge(Edge edge) {

        _edges.put(edge.getNode().getLabel(), edge);
    }

    /**
     * Add new edge to node.
     * @param node Target node
     */
    void addEdge(Node node) {

        Edge edge = _edges.get(node.getLabel());
        int nEdgesOld = _edges.size();
        int nEdgesNew = _edges.size() + 1;

        if (edge != null) {
            // Update existing edge to higher probability.
            edge.strengthen(nEdgesOld, nEdgesNew);
        } else {
            // Edge doesn't exist yet, create and insert.
            edge = new Edge(node, 1.0 / nEdgesNew);
            _edges.put(node.getLabel(), edge);
        }

        // Update all existing edges to lower probability.
        for (Map.Entry<Label, Edge> entry : _edges.entrySet()) {
            Edge e = entry.getValue();
            if (e != edge) {
                e.weaken(nEdgesOld, nEdgesNew);
            }
        }
    }
}

class PlaceholderNode extends Node {

    private final Node _parent;

    PlaceholderNode(Node node) {
        super(node.getLabel());
        _parent = node;
    }

    @Override
    Label getLabel() {
        return _parent.getLabel();
    }

    @Override
    HashMap<Label, Edge> getEdges() {
        return _parent.getEdges();
    }

    @Override
    void addEdge(Edge edge) {
        _parent.addEdge(edge);
    }

    @Override
    void addEdge(Node node) {
        _parent.addEdge(node);
    }
}

/**
 * Represents a sliding window over a phrase.
 */
class SlidingWindow {

    private final List<String>  _phrase;
    private final int           _size;
    private boolean             _isPlaceholder = false;

    /**
     * Create SlidingWindow object.
     * @param phrase Input phrase
     * @param size Window size
     */
    SlidingWindow (List<String>     phrase,
                   int              size) {
        _phrase = new LinkedList<>(phrase);
        _size = size;
    }

    /**
     * @return TRUE if visited placeholder label and not yet reset back to normal mode
     */
    boolean isPlaceholder() { return _isPlaceholder; }

    /**
     * Reset after normal matching has been resumed.
     */
    void resetPlaceholder() { _isPlaceholder = false; }

    /**
     * @return true if the window can slide further
     */
    boolean canSlide() {
        return _phrase.size() >= _size;
    }

    /**
     * Slide window to next position.
     * @return Joint string inside current window
     */
    Label slide() {

        if (_phrase.size() < _size) {
            return null;
        }

        String[] fragments = new String[_size];
        for (int i = 0; i < _size; i++) {
            fragments[i] = _phrase.get(i);
            if (isPlaceholder(fragments[i])) {
                _isPlaceholder = true;
            }
        }

        _phrase.remove(0);

        return new Label(fragments);
    }

    /**
     * @param word
     * @return TRUE if #word is a keyword, otherwise FALSE
     */
    static boolean isPlaceholder(String word) {

        if (word.startsWith("<") &&
            word.endsWith(">")) {
            return true;
        }

        return false;
    }
}

class Config {

    final static Charset CHARSET = Charset.forName("UTF-8");

    final static String JSON_LABEL = "label";
    final static String JSON_WINDOW = "window";
    final static String JSON_EDGES = "edges";
    final static String JSON_FROM = "from";
    final static String JSON_TO = "to";
    final static String JSON_PROBABILITY = "probability";
}

/**
 * Represents a markov chain.
 */
public class MarkovChain {

    private final HashMap<Label, Node>  _nodes = new HashMap<>();
    private final int                   _window;

    /**
     * Create MarkovChain object.
     * @param window Window size
     */
    public MarkovChain(int window) {
        _window = window;
    }

    /**
     * @return Window size
     */
    public int getWindow() {
        return _window;
    }

    /**
     * Load markov model from json array.
     * @param pairs Pairs of nodes with an associated probability between them
     * @return TRUE on success
     */
    boolean load(JSONArray pairs) {

        try {
            for (int i = 0; i < pairs.length(); i++) {
                JSONObject e = pairs.getJSONObject(i);
                JSONArray from = e.getJSONArray(Config.JSON_FROM);
                Label l1 = createLabel(from);
                JSONArray to = e.getJSONArray(Config.JSON_TO);
                Label l2 = createLabel(to);
                double probability = e.getDouble(Config.JSON_PROBABILITY);

                Node n1 = _nodes.get(l1);
                if (null == n1) {
                    n1 = new Node(l1);
                    _nodes.put(n1.getLabel(), n1);
                }
                Node n2 = _nodes.get(l2);
                if (null == n2) {
                    n2 = new Node(l2);
                    _nodes.put(n2.getLabel(), n2);
                }

                Edge edge = new Edge(n2, probability);
                n1.addEdge(edge);
            }
        } catch (JSONException e) {
            // TODO error handling
            return false;
        }

        return true;
    }

    /**
     * Train markov chain with phrase.
     *
     * A phrase needs to be longer than the sliding window, otherwise there are no edges.
     *
     * @param phrase Training phrase
     */
    public void train(List<String> phrase) {

        if (phrase.size() <= _window) {
            return;
        }

        SlidingWindow sw = new SlidingWindow(phrase, _window);
        Label label = sw.slide();
        Node root = _nodes.get(label);
        if (root == null) {
            root = new Node(label);
            _nodes.put(root.getLabel(), root);
        }

        Node n1 = root;
        while (sw.canSlide()) {

            Label l2 = sw.slide();
            Node n2 = _nodes.get(l2);
            if (n2 == null) {
                n2 = new Node(l2);
                _nodes.put(n2.getLabel(), n2);
            }
            n1.addEdge(n2);
            n1 = n2;
        }
    }

    /**
     *  Match phrase against markov chain.
     *
     * A phrase needs to be longer than the sliding window, otherwise there are no edges.
     *
     * Every node in the chain can be a start node. Only full matches of the entire phrase
     * are considered.
     *
     * @param phrase Match phrase
     * @return Average probability: sum of probabilities / number of edges.
     *         Negative value if phrase shorter than two entries such that matching is not possible.
     */
    public double match(List<String> phrase) {

        MatchResults  details;

        // A phrase needs to be longer than the sliding window, otherwise there are no edges
        if (phrase.size() < _window + 1) {
            return -1.0;
        }

        details = new MatchResults();
        double avgProbability = _scanSingleMatch(phrase, 0, details);

        // Strict match, entire phrase needs to be in model.
        if (details.getEntries().size() != 1 ||
            details.getEntries().getFirst().getOffset() != 0 ||
            details.getEntries().getFirst().getPhrase().size() != phrase.size()) {

            return 0.0;
        }

        return avgProbability;
    }

    /**
     * Check if {@code node} has an outgoing label {@code label} taking placeholders into account.
     * @param node Current node
     * @param label Outgoing label to look for
     * @return {@code true} if {@code label} can be matched
     */
    /*
    private boolean _matchPlaceholder(Node  node,
                                      Label label,
                                      boolean firstMatch) {

    //        There are 4 cases to cover:
    //        Label: foo bar baz      # input phrase
    //     Node:  1. foo bar baz      # literal match ... covered outside this function
    //            2. foo bar <x>      # placeholder end of node ......... if firstMatch == true
    //            3. foo <x> baz      # placeholder middle of node ...... not considered at the moment
    //            4. <x> bar baz      # placeholder beginning of node ... if firstMatch == false
    //                                # will fall back to 2. if fails

        TODO

        boolean match = false;
        for (Edge edge : node.getEdges().values()) {

            String[] frags1 = edge.getNode().getLabel().getFragments();
            String[] frags2 = label.getFragments();
            match = false;
            int i;
            for (i = 0; i < frags2.length; i++) {

                // Placeholder matches everything
                if (SlidingWindow.isPlaceholder(frags1[i])) {
                    match = true;
                    break;
                }

                if (!frags1[i].equals(frags2[i])) {
                    break;
                }
            }

            if (i > frags2.length) {
                match = true;
            }

            if (match) {
                break;
            }
        }

        return match;
    }
*/

    /**
     * Scan #phrase for single match against model.
     *
     * After a single sub-phrase has been matched, this will return as soon as the match .
     *
     * @param phrase Match phrase
     * @param phraseOffset Match offset
     * @param details Result details
     * @return Average probability: sum of probabilities / number of edges.
     *         Negative value if phrase shorter than two entries such that matching is not possible.
     */
    private double _scanSingleMatch(List<String>        phrase,
                                    int                 phraseOffset,
                                    MatchResults        details) {

        Node node = null;
        int offset = 0;
        double sumProbabilities = 0.0;

        // A phrase needs to be longer than the sliding window, otherwise there are no edges
        if (phrase.size() < _window + 1) {
            return -1.0;
        }

        // Find first matching node
        SlidingWindow sw = new SlidingWindow(phrase, _window);
        while (sw.canSlide()) {
            Label label = sw.slide();
            node = _nodes.get(label);
            if (node != null) {
                break;
            }
            offset++;
        }
        if (node == null) {
            return 0.0;
        }

        // Match chain
        int nEdges = 0;
        MatchResults.Placeholder placeholder = null;
        while (sw.canSlide()) {

            Label label = sw.slide();
            Edge edge = node.getEdges().get(label);
            if (edge != null) {
                nEdges++;
                sumProbabilities += edge.getProbability();
                node = edge.getNode();
                // Found a match, so reset eventual placeholder scanning
                // and store away placeholder details
                if (placeholder != null) {
                    sw.resetPlaceholder();
                    placeholder = null;
                }
            } else if (false /*_matchPlaceholder(node, label)*/) {

                String word = label.getFragments()[label.getFragments().length - 1];
                if (placeholder == null) {
                    // Starting to match against placeholder
                    placeholder = details.createPlaceholder(word, offset + nEdges);
                    // Phrase eaten up by placeholder always gets probability 1
                    nEdges++;
                    sumProbabilities += 1;
                } else {
                    // Accumulate placeholder phrase
                    placeholder.append(word);
                }
            } else {
                break;
            }
        }

        // Found a match, so reset eventual placeholder scanning
        // and store away placeholder details
        if (placeholder != null) {
            sw.resetPlaceholder();
        }

        // Capture details
        double avgProbability = sumProbabilities / nEdges;
        List<String> subPhrase = phrase.subList(offset, offset + nEdges + _window);
        details.append(subPhrase, phraseOffset + offset, avgProbability, placeholder);

        return avgProbability;
    }

    /**
     * Scan phrase and match sub-phrases against markov chain.
     *
     * A phrase needs to be longer than the sliding window, otherwise there are no edges.
     *
     * Every node in the chain can be a start node. Partial matches are always attempted --
     * if the first word of a phrase doesn't match, next ones are tried. Only the first matching
     * sub-phrase is tested, after the match breaks, no further attempts to match later
     * sub-phrases are made.
     *
     * @param phrase Match phrase
     * @return Average probability: sum of probabilities / number of edges for the best matching sub-phrase
     */
    public double scan(List<String>     phrase,
                       MatchResults     details){

        // A phrase needs to be longer than the sliding window, otherwise there are no edges
        if (phrase.size() < _window + 1) {
            return -1.0;
        }

        if (details == null) {
            details = new MatchResults();
        }

        double avgProbabilityMax = 0.0;
        double avgProbability;
        int offset = 0;
        List<String> subPhrase = phrase;
        do {
            avgProbability = _scanSingleMatch(subPhrase, offset, details);
            if (avgProbability > 0.0) {
                MatchResults.Phrase entry = (MatchResults.Phrase) /* TODO */ details.getEntries().getLast();
                offset = entry.getOffset() + entry.getPhrase().size();
                subPhrase = phrase.subList(offset, phrase.size());
            }
            if (avgProbability > avgProbabilityMax) {
                avgProbabilityMax = avgProbability;
            }
        } while (avgProbability > 0);

        return avgProbabilityMax;    }


    /**
     * Walk the entire markov chain.
     * @param listener Data readout interface
     */
    public void traverse(TraverseInterface listener) {

        // Visit all nodes
        listener.startModel(_window);
        for (Map.Entry<Label, Node> nodeEntry : _nodes.entrySet()) {

            Node n = nodeEntry.getValue();

            // Visit all edges from current node
            for (Map.Entry<Label, Edge> edgeEntry : n.getEdges().entrySet()) {

                // Add edge
                listener.startGraph(n.getLabel().getFragments());
                Edge e = edgeEntry.getValue();
                listener.addEdge(e.getProbability(), e.getNode().getLabel().getFragments());
                listener.endGraph();
            }
        }
        listener.endModel();
    }

    /**
     * Create Label object from JSON string array
     * @param array JSON string array
     * @return Label object
     * @throws JSONException passed up from json library
     */
    private Label createLabel(JSONArray array) throws JSONException {

        Label label = null;

        if (array != null &&
                array.length() > 0) {
            String[] fragments = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                fragments[i] = array.getString(i);
            }
            label = new Label(fragments);
        }

        return label;
    }

    /**
     * Test inner class Label
     * @return TRUE if successful
     */
    public static boolean testLabel() {

        Label l1 = new Label(new String[]{"foo", "bar"});
        Label l2 = new Label(new String[]{"foo", "bar"});
        Label l3 = new Label(new String[]{"foo", "bar", "baz"});
        if (!l1.equals(l2))
            return false;

        if (!l1.equals((Object)l2))
            return false;

        if (l1.equals(l3))
            return false;

        if (l1.equals((Object)l3))
            return false;

        HashMap<Label, String> map = new HashMap<>();
        map.put(l1, "test");

        if (!map.containsKey(l2))
            return false;

        if (map.containsKey(l3))
            return false;

        String test = map.get(l2);
        if (!test.equals("test"))
            return false;

        return true;
    }
}
