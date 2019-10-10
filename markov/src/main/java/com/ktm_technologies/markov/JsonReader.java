package com.ktm_technologies.markov;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

@SuppressWarnings("WeakerAccess")
public class JsonReader {

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
        window = model.optInt(Config.JSON_WINDOW, -1);
        if (window == -1 || edges == null) {
            throw new IllegalArgumentException();
        }

        MarkovChain mc = new MarkovChain(window);
        mc.load(edges);
        return mc;
    }
}
