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

import org.junit.Assert;
import org.junit.Test;

import java.text.BreakIterator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;

public class NlcmdTest {

    private boolean _nlcmd_match = false;
    private boolean _nlcmd_scan = false;
    private boolean _nlcmd_scanDetails = false;

    private boolean _nlcmd_scanTriple1 = false;
    private boolean _nlcmd_scanTriple2 = false;
    private boolean _nlcmd_scanTriple3 = false;

    @Test
    public void nlcmd_match() {

        _nlcmd_match = false;

        // Reset only needed so we can run independent unit tests
        Nlcmd.reset();
        Nlcmd.setOrder(1);

        Nlcmd.action(new String[]{
                "a b c",
                "b c d",
        }, new MatchLambda() {
            @Override
            public void run() {
                // Do nothing, we want to match the other action
            }
        });

        Nlcmd.action(new String[]{
                "c d e",
                "d e f",
        }, new MatchLambda() {
            @Override
            public void run() {
                _nlcmd_match = true;
            }
        });

        Nlcmd.match(Arrays.asList("c d e".split(" ")));
        assertTrue(_nlcmd_match);
    }

    @Test
    public void nlcmd_matchString() {

        _nlcmd_match = false;

        // Reset only needed so we can run independent unit tests
        Nlcmd.reset();
        Nlcmd.setOrder(1);

        Nlcmd.action(new String[]{
                "a b c",
                "b c d",
        }, new MatchLambda() {
            @Override
            public void run() {
                // Do nothing, we want to match the other action
            }
        });

        Nlcmd.action(new String[]{
                "c d e",
                "d e f",
        }, new MatchLambda() {
            @Override
            public void run() {
                _nlcmd_match = true;
            }
        });

        Nlcmd.match("c d e");
        assertTrue(_nlcmd_match);
    }

    @Test
    public void nlcmd_scan() {

        _nlcmd_scan = false;

        // Reset only needed so we can run independent unit tests
        Nlcmd.reset();
        Nlcmd.setOrder(1);

        Nlcmd.action(new String[]{
                "a b c d e",
                "a x b x c x d x",
        }, new ScanLambda() {
            @Override
            public void run(HashMap<List<String>, Double> matches, HashMap<String, List<String>> placeholders) {
                // Do nothing, we want to match the other action
            }
        });

        Nlcmd.action(new String[]{
                "a b c d e",
                "a b x d e",
        }, new ScanLambda() {
            @Override
            public void run(HashMap<List<String>, Double> matches, HashMap<String, List<String>> placeholders) {
                _nlcmd_scan = true;
            }
        });

        Nlcmd.scan(Arrays.asList("a b c d y".split(" ")));
        assertTrue(_nlcmd_scan);
    }

    @Test
    public void nlcmd_scanDetails() {

        _nlcmd_scanDetails = false;

        // Reset only needed so we can run independent unit tests
        Nlcmd.reset();
        Nlcmd.setOrder(1);

        Nlcmd.action(new String[]{
                "a b c d e",
                "a x b x c x d x",
        }, new ScanLambda() {
            @Override
            public void run(HashMap<List<String>, Double> matches,
                            HashMap<String, List<String>> placeholders) {
                // Do nothing, we want to match the other action
            }
        });

        Nlcmd.action(new String[]{
                "a b c d e",
                "a b x d e",
        }, new ScanLambda() {
            @Override
            public void run(HashMap<List<String>, Double> matches,
                            HashMap<String, List<String>> placeholders) {
                _nlcmd_scanDetails = true;

                for (Map.Entry<List<String>, Double> entry : matches.entrySet()) {
                    assertArrayEquals(entry.getKey().toArray(), new String[]{"a", "b", "c", "d"});
                    break;
                }
            }
        });

        Nlcmd.scan(Arrays.asList("a b c d y".split(" ")));
        assertTrue(_nlcmd_scanDetails);
    }

    @Test
    public void nlcmd_scanStringDetails() {

        _nlcmd_scanDetails = false;

        // Reset only needed so we can run independent unit tests
        Nlcmd.reset();
        Nlcmd.setOrder(1);

        Nlcmd.action(new String[]{
                "a b c d e",
                "a x b x c x d x",
        }, new ScanLambda() {
            @Override
            public void run(HashMap<List<String>, Double> matches,
                            HashMap<String, List<String>> placeholders) {
                // Do nothing, we want to match the other action
            }
        });

        Nlcmd.action(new String[]{
                "a b c d e",
                "a b x d e",
        }, new ScanLambda() {
            @Override
            public void run(HashMap<List<String>, Double> matches,
                            HashMap<String, List<String>> placeholders) {
                _nlcmd_scanDetails = true;

                for (Map.Entry<List<String>, Double> entry : matches.entrySet()) {
                    assertArrayEquals(entry.getKey().toArray(), new String[]{"a", "b", "c", "d"});
                    break;
                }
            }
        });

        Nlcmd.scan("a b c d y");
        assertTrue(_nlcmd_scanDetails);
    }

    @Test
    public void nlcmd_scanTriple() {

        _nlcmd_scanTriple1 = false;
        _nlcmd_scanTriple2 = false;
        _nlcmd_scanTriple3 = false;
        Nlcmd.reset();

        Nlcmd.action(new String[]{"hallo und willkommen"},
            new ScanLambda() {
                @Override
                public void run(HashMap<List<String>, Double> matches, HashMap<String, List<String>> placeholders) {
                    _nlcmd_scanTriple1 = true;
                }
        });
        Nlcmd.action(new String[]{"was ist hier los"},
            new ScanLambda() {
                @Override
                public void run(HashMap<List<String>, Double> matches, HashMap<String, List<String>> placeholders) {
                    _nlcmd_scanTriple2 = true;
                }
        });
        Nlcmd.action(new String[]{"das ist ein test"},
            new ScanLambda() {
                @Override
                public void run(HashMap<List<String>, Double> matches, HashMap<String, List<String>> placeholders) {
                    _nlcmd_scanTriple3 = true;
                }
        });

        Nlcmd.scan("hallo und willkommen");
        assertTrue(_nlcmd_scanTriple1);
        assertFalse(_nlcmd_scanTriple2);
        assertFalse(_nlcmd_scanTriple3);
        _nlcmd_scanTriple1 = false;

        Nlcmd.scan("was ist hier los");
        assertFalse(_nlcmd_scanTriple1);
        assertTrue(_nlcmd_scanTriple2);
        assertFalse(_nlcmd_scanTriple3);
        _nlcmd_scanTriple2 = false;

        Nlcmd.scan("das ist ein test");
        assertFalse(_nlcmd_scanTriple1);
        assertFalse(_nlcmd_scanTriple2);
        assertTrue(_nlcmd_scanTriple3);
        _nlcmd_scanTriple3 = false;
    }
}
