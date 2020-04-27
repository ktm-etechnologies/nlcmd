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

import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class UtilsTest {

    private Locale _locale = Locale.getDefault();

    @Test
    public void nlcmd_Util_words1() {

        String phrase = "a b, c";
        List<String> words = Utils.words(phrase, _locale);
        assertEquals(3, words.size());
    }

    @Test
    public void nlcmd_Util_words2() {

        String phrase = "";
        List<String> words = Utils.words(phrase, _locale);
        assertEquals(0, words.size());
    }

    @Test
    public void nlcmd_Util_words3() {

        String phrase = " ";
        List<String> words = Utils.words(phrase, _locale);
        assertEquals(0, words.size());
    }

    @Test
    public void nlcmd_Util_words4() {

        String phrase = " a";
        List<String> words = Utils.words(phrase, _locale);
        assertEquals(1, words.size());
    }

    @Test
    public void nlcmd_Util_words5() {

        String phrase = "<a> b c";
        List<String> words = Utils.words(phrase, _locale);
        assertEquals(3, words.size());
    }

    @Test
    public void nlcmd_Util_words51() {

        String phrase = " <a> b c";
        List<String> words = Utils.words(phrase, _locale);
        assertEquals(3, words.size());
    }

    @Test
    public void nlcmd_Util_words6() {

        String phrase = "a <b> c";
        List<String> words = Utils.words(phrase, _locale);
        assertEquals(3, words.size());
    }

    @Test
    public void nlcmd_Util_words7() {

        String phrase = "a b <c>";
        List<String> words = Utils.words(phrase, _locale);
        assertEquals(3, words.size());
    }

    @Test
    public void nlcmd_Util_words8() {

        String phrase = "a b <c> ";
        List<String> words = Utils.words(phrase, _locale);
        assertEquals(3, words.size());
    }
}