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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Static class providing facilities to read a markov model from JSON data.
 */
@SuppressWarnings("WeakerAccess")
public class JsonReader {

    /**
     * Read a markov model from JSON data.
     * @param in JSON data stream
     * @return The reconstructed {@link com.ktm_technologies.nlcmd.MarkovChain}.
     * @throws Exception In case reading, parsing or constructing the markov chain fails.
     */
    public static MarkovChain read(InputStream in) throws Exception {

        byte[] buf = new byte[4096];
        String content;

        int size;
        StringBuilder sb = new StringBuilder();
        while ((size = in.read(buf)) != -1) {
            sb.append(new String(buf, 0, size, Config.CHARSET));
        }
        content = sb.toString();

        JSONObject model;
        JSONArray edges;
        int window;
        model = new JSONObject(content);
        edges = model.optJSONArray(Config.JSON_EDGES);
        window = model.optInt(Config.JSON_ORDER, -1);
        if (window == -1 || edges == null) {
            throw new IllegalArgumentException();
        }

        MarkovChain mc = new MarkovChain(window);
        mc.load(edges);
        return mc;
    }
}
