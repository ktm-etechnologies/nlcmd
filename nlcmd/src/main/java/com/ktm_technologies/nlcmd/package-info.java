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

/**
 * <h1>Natural language phrase matching for Android</h1>
 *
 * <p>Android supports a number of voice control features, but enabling an individual
 * app for fine grained voice control is not straight forward. This library helps to
 * achieve that.</p>
 *
 * Features include
 * <ul>
 *   <li>Fast and efficient markov-chain based matching.</li>
 *   <li>Simple API for training markov chains from an string array of possible
 *   commands variations or loading models from JSON data.</li>
 *   <li>Support for generic terms such as locations or objects, such that they
 *   don't need to be enumerated in the training data.</li>
 *   <li>Support for creating <a href="https://www.graphviz.org">Graphviz Dot</a>
 *   representations of models for visualization and debugging.</li>
 * </ul>
 * The code is in proof-of-concept stage and feedback is welcome.
 * <ul>
 *   <li>Github page: <a href="https://github.com/ktm-technologies/markov-phrase-matching-android/">https://github.com/ktm-technologies/markov-phrase-matching-android/</a></li>
 *   <li>API reference: <a href="https://ktm-technologies.github.io/markov-phrase-matching-android-doc/">https://ktm-technologies.github.io/markov-phrase-matching-android-doc/</a></li>
 * </ul>
 */
package com.ktm_technologies.nlcmd;
