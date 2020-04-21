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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@SuppressWarnings("unused")
public class MarkovChainTest {

    private final static int _ORDER = 1;

    @Test
    public void markov_ctor() {
        MarkovChain mc = new MarkovChain(MarkovChainTest._ORDER);
        assertNotNull(mc);
    }

    @Test
    public void markov_scanEmptyEmpty() {
        MarkovChain mc = new MarkovChain(MarkovChainTest._ORDER);
        double result = mc.scan(new LinkedList<String>(), null);
        assertEquals(-1.0, result, 0.0001);
    }

    @Test
    public void markov_matchEmptyEmpty() {
        MarkovChain mc = new MarkovChain(MarkovChainTest._ORDER);
        double result = mc.match(new LinkedList<String>());
        assertEquals(-1.0, result, 0.0001);
    }

    @Test
    public void markov_matchEmptyModel() {
        MarkovChain mc = new MarkovChain(MarkovChainTest._ORDER);
        List<String> phrase = Arrays.asList("foo", "bar");
        double result = mc.match(phrase);
        assertEquals(0.0, result, 0.0001);
    }

    @Test
    public void markov_scanEmptyModel() {
        MarkovChain mc = new MarkovChain(MarkovChainTest._ORDER);
        List<String> phrase = Arrays.asList("foo", "bar");
        double result = mc.scan(phrase, null);
        assertEquals(0.0, result, 0.0001);
    }

    @Test
    public void markov_createChain() {
        MarkovChain mc = createFooBarBazChain();
        assertNotNull(mc);
    }

    @Test
    public void markov_matchSingle() {
        MarkovChain mc = createFooBarBazChain();
        List<String> phrase = new LinkedList<>(Arrays.asList("foo", "bar", "baz"));
        double result = mc.match(phrase);
        assertEquals(1.0, result, 0.0001);
    }

    @Test
    public void markov_matchLongW1() {
        MarkovChain mc = new MarkovChain(1);
        List<String> model = Arrays.asList("a b c d".split(" "));
        mc.train(model);
        List<String> phrase = Arrays.asList("a b c d e".split(" "));
        double result = mc.match(phrase);
        assertEquals(0.0, result, 0.0001);
    }

    @Test
    public void markov_scanLongW1() {
        MarkovChain mc = new MarkovChain(1);
        List<String> model = Arrays.asList("a b c d".split(" "));
        mc.train(model);
        List<String> phrase = Arrays.asList("a b c d e".split(" "));
        double result = mc.scan(phrase, null, null);
        assertEquals(1.0, result, 0.0001);
    }

    @Test
    public void markov_scanSingleFull() {
        MarkovChain mc = createFooBarBazChain();
        List<String> phrase = new LinkedList<>(Arrays.asList("foo", "bar", "baz"));
        double result = mc.scan(phrase, null);
        assertEquals(1.0, result, 0.0001);
    }

    @Test
    public void markov_scanSingleStart() {
        MarkovChain mc = createFooBarBazChain();
        List<String> phrase = new LinkedList<>(Arrays.asList("foo", "bar"));
        double result = mc.scan(phrase, null);
        assertEquals(1.0, result, 0.0001);
    }

    @Test
    public void markov_scanSingleEnd() {
        MarkovChain mc = createFooBarBazChain();
        List<String> phrase = new LinkedList<>(Arrays.asList("bar", "baz"));
        double result = mc.scan(phrase, null);
        assertEquals(1.0, result, 0.0001);
    }

    @Test
    public void markov_scanSingleFullLong() {
        MarkovChain mc = createFooBarBazChain();
        List<String> phrase = new LinkedList<>(Arrays.asList("foo", "bar", "baz", "maman"));
        double result = mc.scan(phrase, null);
        // 2 out of 3 edges with probability 1.0 match
        assertEquals(1.0, result, 0.0001);
    }

    @Test
    public void markov_scanSingleStartLong() {
        MarkovChain mc = createFooBarBazChain();
        List<String> phrase = new LinkedList<>(Arrays.asList("foo", "bar", "maman"));
        double result = mc.scan(phrase, null);
        // 1 out of 2 edges with probability 1.0 match
        assertEquals(1.0, result, 0.0001);
    }

    @Test
    public void markov_scanSingleEndLong() {
        MarkovChain mc = createFooBarBazChain();
        List<String> phrase = new LinkedList<>(Arrays.asList("bar", "baz", "maman"));
        double result = mc.scan(phrase, null);
        // 1 out of 2 edges with probability 1.0 match
        assertEquals(1.0, result, 0.0001);
    }

    @Test
    public void markov_scanDetails() {
        MarkovChain mc = new MarkovChain(MarkovChainTest._ORDER);
        List<String> model = new LinkedList<>(Arrays.asList("a", "b", "c", "d", "e", "f"));
        List<String> phrase = new LinkedList<>(Arrays.asList("x", "y", "c", "d", "e", "z"));
        Result details = new Result();
        mc.train(model);
        mc.scan(phrase, details);
        Result.Phrase entry = details.getEntries().getFirst();
        assertArrayEquals(entry.getPhrase().toArray(), new String[] {"c", "d", "e"});
        assertEquals(entry.getAvgProbability(), 1.0, 0.0001);
        assertEquals(entry.getOffset(), 2);
    }

    @Test
    public void markov_scanDetailsPublic() {
        MarkovChain mc = new MarkovChain(MarkovChainTest._ORDER);
        List<String> model = new LinkedList<>(Arrays.asList("a", "b", "c", "d", "e", "f"));
        List<String> phrase = new LinkedList<>(Arrays.asList("x", "y", "c", "d", "e", "z"));
        Result details = new Result();
        mc.train(model);
        HashMap<List<String>, Double> matches = new HashMap<>();
        mc.scan(phrase, matches, null);
        for (Map.Entry<List<String>, Double> entry : matches.entrySet()) {
            assertArrayEquals(entry.getKey().toArray(), new String[] {"c", "d", "e"});
            assertEquals(entry.getValue(), 1.0, 0.0001);
            break;
        }
    }

    @Test
    public void markov_scanSubPhrases() {
        MarkovChain mc = new MarkovChain(MarkovChainTest._ORDER);
        List<String> model = new LinkedList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
        List<String> match = new LinkedList<>(Arrays.asList("x", "b", "c", "y", "d", "e", "f", "z"));
        Result details = new Result();
        mc.train(model);
        mc.scan(match, details);
        Result.Phrase entry;
        // First match
        entry = details.getEntries().getFirst();
        assertArrayEquals(entry.getPhrase().toArray(), new String[] {"b", "c"});
        assertEquals(entry.getAvgProbability(), 1.0, 0.0001);
        assertEquals(entry.getOffset(), 1);
        // Second match
        entry = details.getEntries().getLast();
        assertArrayEquals(entry.getPhrase().toArray(), new String[] {"d", "e", "f"});
        assertEquals(entry.getAvgProbability(), 1.0, 0.0001);
        assertEquals(entry.getOffset(), 4);
    }

    @Test
    public void markov_matchW2() {
        MarkovChain mc = MarkovChainTest.createFoxChainW2();
        List<String> phrase = new LinkedList<>(Arrays.asList("the quick brown fox jumps over the lazy dog".split( " ")));
        double result = mc.match(phrase);
        assertEquals(1.0, result, 0.0001);
    }

    @Test
    public void markov_scanW2Full() {
        MarkovChain mc = MarkovChainTest.createFoxChainW2();
        List<String> phrase = new LinkedList<>(Arrays.asList("the quick brown fox jumps over the lazy dog".split( " ")));
        Result details = new Result();
        double result = mc.scan(phrase, details);
        assertEquals(1.0, result, 0.0001);
        assertEquals(1, details.getEntries().size());
    }

    @Test
    public void markov_scanW2Sub() {
        MarkovChain mc = MarkovChainTest.createFoxChainW2();
        List<String> phrase = new LinkedList<>(Arrays.asList("fox jumps over".split( " ")));
        Result details = new Result();
        double result = mc.scan(phrase, details);
        assertEquals(1.0, result, 0.0001);
        assertEquals(1, details.getEntries().size());
    }

    @Test
    public void markov_scanW2EndPlaceholder() {

        MarkovChain mc = MarkovChainTest.createPlaceholderEndChainW2();
        // OutputStream out = new PrintStream(System.out);
        // DotWriter writer = new DotWriter("Location", out);
        // mc.traverse(writer);
        // out.close();

        List<String> phrase = new LinkedList<>(Arrays.asList("erstelle route nach Munderfing".split( " ")));
        Result details = new Result();
        double result = mc.scan(phrase, details);
        assertEquals(1.0, result, 0.0001);
        assertEquals("<location>", details.getEntries().getFirst().getPlaceholder().getToken());
        assertEquals("Munderfing", details.getEntries().getFirst().getPlaceholder().getPhrase().get(0));
    }

    @Test
    public void markov_scanW2MidPlaceholder() {

        MarkovChain mc = MarkovChainTest.createPlaceholderMidChainW2();
        String[] places = { "Munderfing", "St. Georgen", "Höflein an der hohen Wand"};
        for (String place : places) {

            String s = "füge wegpunkt in " + place + " zusätzlich ein";
            List<String> phrase = new LinkedList<>(Arrays.asList(s.split( " ")));
            Result details = new Result();
            double result = mc.scan(phrase, details);
            assertEquals(1.0, result, 0.0001);
            assertEquals("<location>", details.getEntries().getFirst().getPlaceholder().getToken());
            // Fold phrase matched by placeholder back to location name string for comparison
            String place2 = "";
            List<String> phrase2 = details.getEntries().getFirst().getPlaceholder().getPhrase();
            for (int i = 0; i < phrase2.size(); i++) {
                //noinspection StringConcatenationInLoop
                place2 += phrase2.get(i);
                if (i < phrase2.size() - 1) {
                    place2 += " ";
                }
            }
            assertEquals(place, place2);
        }
    }

    @Test
    public void markov_testLabel() {

        boolean ret = MarkovChain.testLabel();
        assertTrue(ret);
    }

    static MarkovChain createFooBarBazChain() {
        MarkovChain mc = new MarkovChain(MarkovChainTest._ORDER);
        List<String> phrase = new LinkedList<>(Arrays.asList("foo", "bar", "baz"));
        mc.train(phrase);
        return mc;
    }

    @SuppressWarnings("unchecked")
    static MarkovChain createDiamondChain() {
        MarkovChain mc = new MarkovChain(MarkovChainTest._ORDER);
        Object[] phrases = {
                new LinkedList<>(Arrays.asList("a", "b", "c")),
                new LinkedList<>(Arrays.asList("a", "d", "c")),
                new LinkedList<>(Arrays.asList("a", "e", "c"))
        };
        for (Object phrase : phrases) {
            mc.train((List<String>) phrase);
        }
        return mc;
    }

    static MarkovChain createFoxChainW1() {
        MarkovChain mc = new MarkovChain(1);
        List<String> phrase = new LinkedList<>(Arrays.asList("the quick brown fox jumps over the lazy dog".split( " ")));
        mc.train(phrase);
        return mc;
    }

    static MarkovChain createFoxChainW2() {
        MarkovChain mc = new MarkovChain(2);
        List<String> phrase = new LinkedList<>(Arrays.asList("the quick brown fox jumps over the lazy dog".split( " ")));
        mc.train(phrase);
        return mc;
    }

    static MarkovChain createFishChainW1() {
        MarkovChain mc = new MarkovChain(1);
        List<String> phrase = new LinkedList<>(Arrays.asList("one fish two fish red fish blue fish".split( " ")));
        mc.train(phrase);
        return mc;
    }

    static MarkovChain createFishChainW2() {
        MarkovChain mc = new MarkovChain(2);
        List<String> phrase = new LinkedList<>(Arrays.asList("one fish two fish red fish blue fish".split( " ")));
        mc.train(phrase);
        return mc;
    }

    private static MarkovChain createPlaceholderEndChainW2() {
        MarkovChain mc = new MarkovChain(2);
        List<String> phrase = new LinkedList<>(Arrays.asList("erstelle route nach <location>".split( " ")));
        mc.train(phrase);
        return mc;
    }

    private static MarkovChain createPlaceholderMidChainW2() {
        MarkovChain mc = new MarkovChain(2);
        List<String> phrase = new LinkedList<>(Arrays.asList("füge wegpunkt in <location> zusätzlich ein".split( " ")));
        mc.train(phrase);
        return mc;
    }

    static MarkovChain createMunderfingChainW1() {
        MarkovChain mc = new MarkovChain(1);
        List<String> phrase = new LinkedList<>(Arrays.asList("Wegpunkt in Munderfing erstellen".split( " ")));
        mc.train(phrase);
        phrase = new LinkedList<>(Arrays.asList("Erstelle Route nach Munderfing".split( " ")));
        mc.train(phrase);
        phrase = new LinkedList<>(Arrays.asList("Navigation nach Munderfing starten".split( " ")));
        mc.train(phrase);
        return mc;
    }
}