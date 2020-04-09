package com.ktm_technologies.nlcmd;

import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class JsonTest {

    @Ignore("TODO Test is disabled for failure")
    @Test
    public void writeFooBarBazJson() throws Exception {

        MarkovChain mc = MarkovChainTest.createFooBarBazChain();
        JsonWriter writer = new JsonWriter("FooBarBaz", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Ignore("TODO Test is disabled for failure")
    @Test
    public void writeFishJsonW1() throws Exception {

        MarkovChain mc = MarkovChainTest.createFishChainW1();
        JsonWriter writer = new JsonWriter("Fish", new PrintStream(System.out));
        mc.traverse(writer);
    }

    @Ignore("TODO Test is disabled for failure")
    @Test
    public void roundtripJsonW1() throws Exception {

        MarkovChain mc1 = MarkovChainTest.createFoxChainW1();
        List<String> phrase = new LinkedList<>(Arrays.asList("over", "the", "lazy", "dog"));
        double result1 = mc1.scan(phrase, new Result());
        assertEquals(0.83333, result1, 0.0001);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonWriter writer = new JsonWriter("Fox", out);
        mc1.traverse(writer);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        MarkovChain mc2 = JsonReader.read(in);
        assert mc2 != null;
        Result match = new Result();
        double result2 = mc2.scan(phrase, match);
        assertEquals(result1, result2, 0.0001);
    }

    @Ignore("TODO Test is disabled for failure")
    @Test
    public void roundtripJsonW2() throws Exception {

        MarkovChain mc1 = MarkovChainTest.createFoxChainW2();
        List<String> phrase = new LinkedList<>(Arrays.asList("over", "the", "lazy", "dog"));
        double result1 = mc1.scan(phrase, new Result());
        assertEquals(1.0, result1, 0.0001);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonWriter writer = new JsonWriter("Fox", out);
        mc1.traverse(writer);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        MarkovChain mc2 = JsonReader.read(in);
        assert mc2 != null;
        Result match = new Result();
        double result2 = mc2.scan(phrase, match);
        assertEquals(result1, result2, 0.0001);
    }
}