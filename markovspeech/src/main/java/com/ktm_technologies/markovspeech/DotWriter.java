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

package com.ktm_technologies.markovspeech;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Used for writing model to Graphviz dot format.
 */
@SuppressWarnings("SpellCheckingInspection")
public class DotWriter implements TraverseInterface {

    private final String        _name;
    private final PrintStream   _out;

    public DotWriter(String         name,
                     OutputStream   out) {
        _name = name;
        _out = new PrintStream(out);
    }

    @Override
    public void startModel(int window) {

        _out.println("digraph " + _name + " {");
    }

    @Override
    public void endModel() {

        _out.println("}");
    }

    @Override
    public void startGraph(String[] labelFragments) {

        String label = createLabel(labelFragments);
        _out.print("  \"" + label + "\"");
    }

    @Override
    public void endGraph() {

        _out.println();
    }

    @Override
    public void addEdge(double      probability,
                        String[]    labelFragments) {

        String label = createLabel(labelFragments);
        String p = String.format(Locale.US, "%.2f",probability);

        _out.print(" -> \"" + label + "\" [ label=\"" + p + "\" ];");
    }

    /**
     * Turns String array into node label string
     * @param fragments String array
     * @return node label string
     */
    private String createLabel(String[] fragments) {

        StringBuilder sb = new StringBuilder();
        if (fragments != null) {
            for (int i = 0; i < fragments.length; i++) {
                sb.append(fragments[i]);
                if (i < fragments.length - 1) {
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }
}
