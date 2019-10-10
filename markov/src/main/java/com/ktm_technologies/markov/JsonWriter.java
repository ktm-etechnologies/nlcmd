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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;

/**
 * Used for writing model JSON format.
 */
public class JsonWriter implements Stream {

    private final String        _name;
    private final JSONObject    _json;
    private final OutputStream  _out;

    private JSONArray   _edges = null;
    private JSONObject  _edge;

    /**
     * Create JsonWriter object.
     * @param name Markov model name, for convenience only
     * @param out Output stream
     */
    public JsonWriter(String         name,
                      OutputStream   out) {
        _name = name;
        _json = new JSONObject();
        _out = out;
    }

    @Override
    public void startModel(int window) throws Exception {

        _json.put(Config.JSON_LABEL, _name);
        _json.put(Config.JSON_WINDOW, window);
    }

    @Override
    public void endModel() throws Exception {

        _edges = null;
        _out.write(_json.toString(2).getBytes(Config.CHARSET));
    }

    @Override
    public void startGraph(String[] labelFragments) {

        try {

            if (null == _edges) {
                _edges = new JSONArray();
                _json.put(Config.JSON_EDGES, _edges);
            }

            _edge = new JSONObject();
            JSONArray from = createLabelArray(labelFragments);
            _edge.put(Config.JSON_FROM, from);
            _edges.put(_edges.length(), _edge);

        } catch (JSONException e) {
            // TODO error handling
        }
    }

    @Override
    public void endGraph() {

        _edge = null;
    }

    @Override
    public void addEdge(double      probability,
                        String[]    labelFragments) {

        try {

            JSONArray to = createLabelArray(labelFragments);
            _edge.put(Config.JSON_TO, to);
            _edge.put(Config.JSON_PROBABILITY, probability);

        } catch (JSONException e) {
            // TODO error handling
        }
    }

    /**
     * Turns String array into json array
     * @param fragments String array
     * @return JSON array or null
     */
    private JSONArray createLabelArray(String[] fragments) {

        JSONArray array = new JSONArray();
        if (fragments != null) {
            for (String s : fragments) {
                array.put(s);
            }
        }
        return array;
    }
}
