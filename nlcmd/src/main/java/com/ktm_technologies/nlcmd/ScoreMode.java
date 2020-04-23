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

/**
 * Markov chain scoring mode used for calculating the best matching action.
 * TODO Detailed doc
 */
public enum ScoreMode {

    /**
     * The sub-phrase with the highest average probability is selected, regardless of
     * sub-phrase length.
     */
    HIGHEST_AVG,

    /**
     * The longest matching sub-phrase is selected and if shorter than total phrase length,
     * scaled to relative length.
     */
    LONGEST_AVG_REL,

    /**
     * Respect input phrase morphology.
     * TODO
     */
    LONGEST_AVG_REL_MOR
}
