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

/**
 * Interface used to walk a markov chain.
 */
public interface TraverseInterface {

    /**
     * Invoked when starting to iterate the model.
     * @param window Window size
     */
    void startModel(int window);

    /**
     * Invoked when iteration of the model is finished.
     */
    void endModel();

    /**
     * Invoked when starting to iterate a graph inside the model.
     */
    void startGraph(String[] labelFragments);

    /**
     * Invoked when iteration of the current graph is finished.
     */
    void endGraph();

    /**
     * Invoked when visiting an edge in the current graph.
     */
    void addEdge(double probability,
                 String[] labelFragments);
}
