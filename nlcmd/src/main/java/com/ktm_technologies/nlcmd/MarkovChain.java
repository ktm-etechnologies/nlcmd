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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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
     * @return {@code true} if matching
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
 * Represents edge to {@link Node} in the chain.
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
 * For temporary edge objects during placeholder matching.
 */
class ReflexiveEdge extends Edge {

    /**
     * Create ReflexiveEdge object
     * @param node Target node
     */
    ReflexiveEdge(Node node) {
        super(node, 1.0);
    }
}


/**
 * Represents a node in the chain.
 */
class Node {

    private final HashMap<Label, Edge>  _edges = new HashMap<>();
    private final Label                 _label;

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
     * Associate this node with a training phrase.
     * Not used in the default implementation.
     * @param phrase Training phrase
     * @param offset Offset of node in training phrase
     */
    void associate(List<String> phrase, int offset) {}

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
     * Query whether an edge to a node labelled "label" exists
     * @param label Target node label
     * @return Edge object if found, or null
     */
    Edge queryEdge(Label label, Result details, int offset) {

        Edge edge =  _edges.get(label);

        // Fall back to placeholder matching.
        if (null == edge) {

            // 1)
            // Find node of form <prefix> <placeholder> <last-fragment>
            // If found, this terminates consuming input into a placeholder
            for (Edge e : _edges.values()) {

                String[] frags1 = e.getNode().getLabel().getFragments();
                String[] frags2 = label.getFragments();
                int last = frags1.length - 1;

                // Placeholder
                if (last > 0 &&
                    !SlidingWindow.isPlaceholder(frags1[last - 1])) {
                    continue;
                }

                // Last fragment
                if (last > 0 &&
                    frags1[last].equals(frags2[last])) {
                    edge = e;
                    break;
                }
            }

            // 2)
            // Find node of form <prefix> <placeholder>
            // This would start consuming into a placeholder
            for (Edge e : _edges.values()) {

                String[] frags1 = e.getNode().getLabel().getFragments();
                String[] frags2 = label.getFragments();
                int last = frags1.length - 1;

                // Match prefix
                int i;
                for (i = 0; i < last - 1; i++) {
                    if (!frags1[i].equals(frags2[i])) {
                        break;
                    }
                }
                if (i < last - 1) {
                    continue;
                }

                // Ensure placeholder
                if (last >= 0 &&
                    SlidingWindow.isPlaceholder(frags1[last])) {
                    edge = e;
                    details.createPlaceholder(frags1[last], offset);
                    details.appendPlaceholder(frags2[last]);
                    break;
                }
            }

            // 3)
            // If no node found above, make sure this node can consume
            // input in the placeholder
            if (null == edge) {

                String[] frags1 = _label.getFragments();
                String[] frags2 = label.getFragments();
                int last = frags1.length - 1;

                // Ensure placeholder
                if (SlidingWindow.isPlaceholder(frags1[last])) {
                    // If matching, return reflexive edge back to self,
                    // to consume more input and then try to find subsequent node
                    edge = new ReflexiveEdge(this);
                    details.appendPlaceholder(frags2[last]);
                }
            }

            // Reset if placeholder matching failed
            if (null == edge) {
                details.resetPlaceholder();
            }
        }

        return edge;
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


/**
 * Represents a sliding window over a phrase.
 */
class SlidingWindow {

    private final List<String>  _phrase;
    private final int           _size;
    private int                 _offset;

    /**
     * Create SlidingWindow object.
     * @param phrase Input phrase
     * @param size Window size
     */
    SlidingWindow (List<String>     phrase,
                   int              size) {
        _phrase = new LinkedList<>(phrase);
        _size = size;
        _offset = -1;
    }

    /**
     * @return {@code true} if the window can slide further
     */
    boolean canSlide() {
        return _phrase.size() >= _size;
    }

    /**
     * @return Offset of window in current phrase,
     *         will be < 0 before first invocation of SlidingWindow#slide()
     */
    int getOffset() { return _offset; }

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
        }

        _phrase.remove(0);
        _offset++;

        return new Label(fragments);
    }

    /**
     * @param token Input word
     * @return {@code true} if {@code token} is a placeholder
     */
    @SuppressWarnings("RedundantIfStatement")
    static boolean isPlaceholder(String token) {

        if (token.startsWith("<") &&
            token.endsWith(">")) {
            return true;
        }

        return false;
    }
}


/**
 * Interface used to walk a markov chain.
 */
interface Stream {

    /**
     * Invoked when starting to iterate the model.
     * @param window Window size
     */
    void startModel(int window) throws Exception;

    /**
     * Invoked when iteration of the model is finished.
     */
    void endModel() throws Exception;

    /**
     * Invoked when starting to iterate a graph inside the model.
     * @param labelFragments Node label
     */
    void startGraph(String[] labelFragments) throws Exception;

    /**
     * Invoked when iteration of the current graph is finished.
     */
    void endGraph() throws Exception;

    /**
     * Invoked when visiting an edge in the current graph.
     * @param probability Probability for the edge
     * @param labelFragments Target node label
     */
    void addEdge(double probability,
                 String[] labelFragments)
        throws Exception;
}

/**
 * Class for creating nodes, can be overridden to create custom Node subclasses.
 */
class MarkovChainMixin {

    private Random  _random = new Random();

    /**
     * Create Node instance
     * @param label Label asocciated to the node
     * @return New Node instance
     */
    Node create(Label label) {

        return new Node(label);
    }

    /**
     * Initialize new query.
     * @param phrase Query phrase
     * @return Unique ID for the new query
     */
    int initQuery(List<String> phrase) {

        return _random.nextInt();
    }

    /**
     * Individual query step.
     * @param id Unique query ID
     * @param node Node that is queried
     * @param edge Outgoing Edge objects
     */
    void updateQuery(int id, Node node, Edge edge) {

    }

    /**
     * Complete the query
     * @param id Unique query ID
     * @param success Whether the query had at least one successful match
     */
    void finishQuery(int id, boolean success) {

    }
}

/**
 * Static configuration constants.
 */
class Config {

    final static Charset CHARSET = Charset.forName("UTF-8");

    final static String JSON_LABEL = "label";
    final static String JSON_ORDER = "order";
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
    private final int                   _order;
    private final ArrayList<List<String>> phraseList=new ArrayList<>();
    private MarkovChainMixin            _mixin;

    public double matchingFaktor(List<String> resultingPhrase){
        double faktor;
        double tempCount=0;
        int tempN=0;
        for(int n=0;n<phraseList.size();n++){
            List<String> testPhrase=phraseList.get(n);
            int p=0;
            int counter=0;
            for(int k=0;k<testPhrase.size();k++){
                if(testPhrase.get(k).equals(resultingPhrase.get(p))){
                    p++;
                    if(p>counter){
                        counter=p;
                    }
                    if(p>=resultingPhrase.size()){
                        break;
                    }
                }
                else{
                    p=0;
                }
            }
            if(counter>tempCount) {
                tempN=n;
                tempCount = counter;
            }
        }
        faktor=tempCount/phraseList.get(tempN).size();
        return faktor;
    }
    /**
     * Create MarkovChain object.
     * @param order Markov chain order, that is number of relevant previous steps when matching
     */
    public MarkovChain(int order) {

        _order = order;
        setMixin(null);
    }

    /**
     * @return Markov chain order, that is number of relevant previous steps when matching
     */
    @SuppressWarnings("unused")
    public int getOrder() {
        return _order;
    }

    /**
     * @return Node customization and scoring mixin instance
     */
    @SuppressWarnings("WeakerAccess")
    public MarkovChainMixin getMixin() {

        return _mixin;
    }

    /**
     * Set custom customization and scoring mixin.
     * @param mixin Mixin instance or NULL to reset to default
     */
    @SuppressWarnings("WeakerAccess")
    public void setMixin(MarkovChainMixin mixin) {

        if (mixin == null) {
            _mixin = new MarkovChainMixin();
        } else {
            _mixin = mixin;
        }
    }

    /**
     * Load markov model from json array.
     * @param pairs Pairs of nodes with an associated probability between them
     * @throws JSONException if failed
     */
    void load(JSONArray pairs) throws JSONException {

        for (int i = 0; i < pairs.length(); i++) {
            JSONObject e = pairs.getJSONObject(i);
            JSONArray from = e.getJSONArray(Config.JSON_FROM);
            Label l1 = createLabel(from);
            JSONArray to = e.getJSONArray(Config.JSON_TO);
            Label l2 = createLabel(to);
            double probability = e.getDouble(Config.JSON_PROBABILITY);

            Node n1 = _nodes.get(l1);
            if (null == n1) {
                n1 = _mixin.create(l1);
                _nodes.put(n1.getLabel(), n1);
            }
            Node n2 = _nodes.get(l2);
            if (null == n2) {
                n2 = _mixin.create(l2);
                _nodes.put(n2.getLabel(), n2);
            }

            Edge edge = new Edge(n2, probability);
            n1.addEdge(edge);
        }
    }

    /**
     * Train markov chain with phrase.
     *
     * A phrase needs to be longer than the sliding window, otherwise there are no edges.
     *
     * @param phrase Training phrase
     */
    public void train(List<String> phrase) {

        if (phrase.size() <= _order) {
            return;
        }
        phraseList.add(phrase);
        SlidingWindow sw = new SlidingWindow(phrase, _order);
        Label label = sw.slide();
        Node root = _nodes.get(label);
        if (root == null) {
            root = _mixin.create(label);
            _nodes.put(root.getLabel(), root);
        }
        root.associate(phrase, sw.getOffset());

        Node n1 = root;
        while (sw.canSlide()) {

            Label l2 = sw.slide();
            Node n2 = _nodes.get(l2);
            if (n2 == null) {
                n2 = _mixin.create(l2);
                _nodes.put(n2.getLabel(), n2);
            }
            n2.associate(phrase, sw.getOffset());
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
    @SuppressWarnings("WeakerAccess")
    public double match(List<String> phrase) {

        Result details;

        // A phrase needs to be longer than the sliding window, otherwise there are no edges
        if (phrase.size() < _order + 1) {
            return -1.0;
        }

        details = new Result();
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
     * Scan {@code phrase} for single match against model.
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
                                    Result details) {

        Node node = null;
        int offset = 0;
        double sumProbabilities = 0.0;

        // A phrase needs to be longer than the sliding window, otherwise there are no edges
        if (phrase.size() < _order + 1) {
            return -1.0;
        }

        int queryId = _mixin.initQuery(phrase);

        // Find first matching node
        SlidingWindow sw = new SlidingWindow(phrase, _order);
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
        while (sw.canSlide()) {
            Label label = sw.slide();
            Edge edge = node.queryEdge(label, details, offset + nEdges);
            if (edge != null) {
                _mixin.updateQuery(queryId, node, edge);
                nEdges++;
                sumProbabilities += edge.getProbability();
                node = edge.getNode();
            } else {
                break;
            }
        }

        _mixin.finishQuery(queryId, nEdges > 0);

        // Capture details
        double avgProbability = sumProbabilities /nEdges ;
        List<String> subPhrase = phrase.subList(offset, offset + nEdges + _order);
        details.append(subPhrase, phraseOffset + offset, avgProbability);

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
     * @param matches Map of phrase : probability, may be null
     * @param placeholders Map of placeholder : variable, may be null
     * @return Average probability: sum of probabilities / number of edges for the best matching sub-phrase
     */
    @SuppressWarnings("WeakerAccess")
    public double scan(List<String>                     phrase,
                       HashMap<List<String>, Double>    matches,
                       HashMap<String, List<String>>    placeholders) {

        Result details = new Result();
        double ret = scan(phrase, details);
        details.extractMatches(matches, placeholders);

        return ret;
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
    double scan(List<String>    phrase,
                Result          details){

            // A phrase needs to be longer than the sliding window, otherwise there are no edges
        if (phrase.size() < _order + 1) {
            return -1.0;
        }

        if (details == null) {
            details = new Result();
        }

        double avgProbabilityMax = 0.0;
        double avgProbability;
        int offset = 0;
        List<String> subPhrase = phrase;
        do {
            avgProbability = _scanSingleMatch(subPhrase, offset, details);
            if (avgProbability > 0.0) {
                Result.Phrase entry = details.getEntries().getLast();
                offset = entry.getOffset() + entry.getPhrase().size();
                subPhrase = phrase.subList(offset, phrase.size());
            }
            if (avgProbability > avgProbabilityMax) {
                avgProbabilityMax = avgProbability;
            }
        } while (avgProbability > 0);

        return avgProbabilityMax;
    }

    /**
     * Walk the entire markov chain.
     * @param listener Data readout interface
     */
    public void traverse(Stream listener) throws Exception {

        // Visit all nodes
        listener.startModel(_order);
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
     * @return {@code true} if successful
     */
    static boolean testLabel() {

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
        //noinspection RedundantIfStatement
        if (!Objects.equals(test, "test"))
            return false;

        return true;
    }
}
