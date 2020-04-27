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

import android.os.CpuUsageInfo;

import java.text.BreakIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Internal static utilities container.
 */
class Utils {

    /**
     * Static-only methods in this class, no instantiation.
     */
    private Utils() {}

    /**
     * Split string into words.
     * @param phrase Input string for splitting up
     * @param locale Language settings
     * @return Linked list of words
     */
    static List<String> words(String phrase, Locale locale) {

        BreakIterator iter = BreakIterator.getWordInstance(locale);
        iter.setText(phrase);
        List<String> l = new LinkedList<>();
        int start = iter.first();
        int end = iter.next();
        while (end != BreakIterator.DONE) {
            if (Character.isLetterOrDigit(phrase.charAt(start)) ||
                phrase.charAt(start) == Config.START_PH) {

                // Find corresponding end placeholder mark
                if (phrase.charAt(start) == Config.START_PH) {
                    while (phrase.charAt(end) != Config.END_PH &&
                           end < phrase.length()) {
                        end++;
                    }
                    if (end >= phrase.length()) {
                        throw new StringIndexOutOfBoundsException("Failed to find closing placeholder mark in :" + phrase);
                    }
                    end++;
                }

                String word = phrase.substring(start, end);
                l.add(word);
            }
            start = end;
            end = iter.following(end);
        }

        return l;
    }
}
