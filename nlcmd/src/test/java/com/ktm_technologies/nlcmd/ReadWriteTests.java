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
    public void writeFooBarBazDot() throws Exception {

        MarkovChain mc = MarkovChainTest.createFooBarBazChain();
        DotWriter writer = new DotWriter("FooBarBaz", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeDiamondDot() throws Exception {

        MarkovChain mc = MarkovChainTest.createDiamondChain();
        DotWriter writer = new DotWriter("Diamond", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeFoxDotW1() throws Exception {

        MarkovChain mc = MarkovChainTest.createFoxChainW1();
        DotWriter writer = new DotWriter("Diamond", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeFoxDotW2() throws Exception {

        MarkovChain mc = MarkovChainTest.createFoxChainW2();
        DotWriter writer = new DotWriter("Diamond", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeFishDotW1() throws Exception {

        MarkovChain mc = MarkovChainTest.createFishChainW1();
        DotWriter writer = new DotWriter("Fish", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeFishDotW2() throws Exception {

        MarkovChain mc = MarkovChainTest.createFishChainW2();
        DotWriter writer = new DotWriter("Fish", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Test
    public void writeMunderfingDot() throws Exception {

        MarkovChain mc = MarkovChainTest.createMunderfingChainW1();
        DotWriter writer = new DotWriter("Fish", new PrintStream(System.out));
        mc.traverse(writer);
    }

}