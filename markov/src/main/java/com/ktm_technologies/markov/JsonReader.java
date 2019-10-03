package com.ktm_technologies.markov;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class JsonReader {

    public static MarkovChain read(InputStream in) {

        byte[] buf = new byte[4096];
        String content;

        try {
            int size;
            StringBuilder sb = new StringBuilder();
            while ((size = in.read(buf)) != -1) {
                sb.append(new String(buf, 0, size, Config.CHARSET));
            }
            content = sb.toString();
        } catch (IOException e) {
            // TODO error handling
            return null;
        }

        JSONObject model;
        JSONArray edges;
        int window;
        try {
            model = new JSONObject(content);
            edges = model.optJSONArray(Config.JSON_EDGES);
            window = model.optInt(Config.JSON_WINDOW, -1);
            if (window == -1 || edges == null) {
                // TODO error handling
                return null;
            }
        } catch (JSONException e) {
            // TODO error handling
            return null;
        }

        MarkovChain mc = new MarkovChain(window);
        return mc.load(edges) ? mc : null;
    }
}
