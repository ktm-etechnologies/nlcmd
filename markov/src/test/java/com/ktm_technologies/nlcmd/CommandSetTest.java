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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@SuppressWarnings("unused")
public class CommandSetTest {

    private final static int _WINDOW = 2;

    @Test
    public void command_matchNavigation() {

        CommandSet cs = new CommandSet();
        cs.put("destination", createDestinationChainW2());
        cs.put("waypoint", createWaypointChainW2());
        cs.put("skip", createSkipWaypointChainW2());

        String key = cs.match(Arrays.asList("load route to Munderfing".split(" ")));
        assertEquals(key, "destination");
    }

    @Test
    public void command_scanNavigation() {

        CommandSet cs = new CommandSet();
        cs.put("destination", createDestinationChainW2());
        cs.put("waypoint", createWaypointChainW2());
        cs.put("skip", createSkipWaypointChainW2());

        LinkedList<String> phrase = new LinkedList<>(Arrays.asList("load route to Munderfing".split(" ")));
        HashMap<List<String>, Double> matches = new HashMap<>();
        String key = cs.scan(phrase, matches, null);
        assertEquals(key, "destination");
        for (Map.Entry<List<String>, Double> entry : matches.entrySet()) {
            assertTrue(phrase.equals(entry.getKey()));
            assertEquals(entry.getValue(), 1.0, 0.0001);
            break;
        }
    }

    static MarkovChain createDestinationChainW2() {
        MarkovChain mc = new MarkovChain(_WINDOW);
        Object[] phrases = {
                new LinkedList<>(Arrays.asList("set destination <location>".split(" "))),
                new LinkedList<>(Arrays.asList("set <location> as destination".split(" "))),
                new LinkedList<>(Arrays.asList("set route to <location>".split(" "))),
                new LinkedList<>(Arrays.asList("go to <location>".split(" "))),
                new LinkedList<>(Arrays.asList("navigate to <location>".split(" "))),
                new LinkedList<>(Arrays.asList("load route to <location>".split(" ")))
        };
        for (Object phrase : phrases) {
            mc.train((List<String>) phrase);
        }
        return mc;
    }

    static MarkovChain createWaypointChainW2() {
        MarkovChain mc = new MarkovChain(_WINDOW);
        Object[] phrases = {
                new LinkedList<>(Arrays.asList("set waypoint in <location>".split(" "))),
                new LinkedList<>(Arrays.asList("add waypoint in <location>".split(" "))),
                new LinkedList<>(Arrays.asList("set <location> as waypoint".split(" "))),
                new LinkedList<>(Arrays.asList("go via <location>".split(" "))),
                new LinkedList<>(Arrays.asList("navigate via <location>".split(" "))),
                new LinkedList<>(Arrays.asList("add detour via <location>".split(" "))),
                new LinkedList<>(Arrays.asList("change route to go via <location>".split(" ")))
        };
        for (Object phrase : phrases) {
            mc.train((List<String>) phrase);
        }
        return mc;
    }

    static MarkovChain createSkipWaypointChainW2() {
        MarkovChain mc = new MarkovChain(_WINDOW);
        Object[] phrases = {
                new LinkedList<>(Arrays.asList("delete next waypoint".split(" "))),
                new LinkedList<>(Arrays.asList("delete waypoint in <location>".split(" "))),
                new LinkedList<>(Arrays.asList("delete upcoming waypoint".split(" "))),
                new LinkedList<>(Arrays.asList("ignore next waypoint".split(" "))),
                new LinkedList<>(Arrays.asList("ignore waypoint in <location>".split(" "))),
                new LinkedList<>(Arrays.asList("ignore upcoming waypoint".split(" "))),
                new LinkedList<>(Arrays.asList("remove next waypoint".split(" "))),
                new LinkedList<>(Arrays.asList("remove waypoint in <location>".split(" "))),
                new LinkedList<>(Arrays.asList("remove upcoming waypoint".split(" "))),
                new LinkedList<>(Arrays.asList("skip next waypoint".split(" "))),
                new LinkedList<>(Arrays.asList("skip waypoint in <location>".split(" "))),
                new LinkedList<>(Arrays.asList("skip upcoming waypoint".split(" ")))
        };
        for (Object phrase : phrases) {
            mc.train((List<String>) phrase);
        }
        return mc;
    }
}

