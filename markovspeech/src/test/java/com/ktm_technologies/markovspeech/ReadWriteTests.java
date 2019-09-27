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


import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ReadWriteTests {

    @Test
    public void writeFooBarBazDot() {

        MarkovChain mc = MarkovChainTest.createFooBarBazChain();
        DotWriter writer = new DotWriter("FooBarBaz", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeDiamondDot() {

        MarkovChain mc = MarkovChainTest.createDiamondChain();
        DotWriter writer = new DotWriter("Diamond", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeFoxDotW1() {

        MarkovChain mc = MarkovChainTest.createFoxChainW1();
        DotWriter writer = new DotWriter("Diamond", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeFoxDotW2() {

        MarkovChain mc = MarkovChainTest.createFoxChainW2();
        DotWriter writer = new DotWriter("Diamond", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeFishDotW1() {

        MarkovChain mc = MarkovChainTest.createFishChainW1();
        DotWriter writer = new DotWriter("Fish", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeFishDotW2() {

        MarkovChain mc = MarkovChainTest.createFishChainW2();
        DotWriter writer = new DotWriter("Fish", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeFooBarBazJson() {

        MarkovChain mc = MarkovChainTest.createFooBarBazChain();
        JsonWriter writer = new JsonWriter("FooBarBaz", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeFishJsonW1() {

        MarkovChain mc = MarkovChainTest.createFishChainW1();
        JsonWriter writer = new JsonWriter("Fish", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void roundtripJsonW1() {

        MarkovChain mc1 = MarkovChainTest.createFoxChainW1();
        List<String> phrase = new LinkedList<>(Arrays.asList("over", "the", "lazy", "dog"));
        double result1 = mc1.scan(phrase, new MatchResults());
        assertEquals(0.83333, result1, 0.0001);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonWriter writer = new JsonWriter("Fox", out);
        mc1.traverse(writer);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        MarkovChain mc2 = JsonReader.read(in);
        MatchResults match = new MatchResults();
        double result2 = mc2.scan(phrase, match);
        assertEquals(result1, result2, 0.0001);
    }

    @Test
    public void roundtripJsonW2() {

        MarkovChain mc1 = MarkovChainTest.createFoxChainW2();
        List<String> phrase = new LinkedList<>(Arrays.asList("over", "the", "lazy", "dog"));
        double result1 = mc1.scan(phrase, new MatchResults());
        assertEquals(1.0, result1, 0.0001);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonWriter writer = new JsonWriter("Fox", out);
        mc1.traverse(writer);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        MarkovChain mc2 = JsonReader.read(in);
        MatchResults match = new MatchResults();
        double result2 = mc2.scan(phrase, match);
        assertEquals(result1, result2, 0.0001);
    }
}