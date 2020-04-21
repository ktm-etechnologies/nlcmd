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
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class MarkovChainApiTest {

    private final static int    _ORDER = 1;
    private boolean             _createNodeFactory_NodeCreated;
    private TestNode            _createNodeSubclass_TestNode;

    @Test
    public void markov_createNodeFactory() {

        _createNodeFactory_NodeCreated = false;

        MarkovChain mc = new MarkovChain(_ORDER);
        mc.setMixin(new MarkovChainMixin() {
            Node create(Label label) {
                _createNodeFactory_NodeCreated = true;
                return new Node(label) {};
            }
        });
        List<String> train = new LinkedList<>(Arrays.asList("foo", "bar", "baz"));
        mc.train(train);

        List<String> phrase = new LinkedList<>(Arrays.asList("foo", "bar", "baz"));
        double result = mc.match(phrase);
        assertEquals(1.0, result, 0.0001);
        assertTrue(_createNodeFactory_NodeCreated);
    }

    @Test
    public void markov_createNodeSubclass() {

        _createNodeSubclass_TestNode = null;

        MarkovChain mc = new MarkovChain(_ORDER);
        mc.setMixin(new MarkovChainMixin() {
            Node create(Label label) {
                _createNodeSubclass_TestNode = new TestNode(label);
                return _createNodeSubclass_TestNode;
            }
        });
        List<String> train = new LinkedList<>(Arrays.asList("foo", "bar", "baz"));
        mc.train(train);

        List<String> phrase = new LinkedList<>(Arrays.asList("foo", "bar", "baz"));
        double result = mc.match(phrase);
        assertEquals(train.hashCode(), phrase.hashCode());
        assertEquals(_createNodeSubclass_TestNode.getPhraseHash(), phrase.hashCode());
        assertEquals(_createNodeSubclass_TestNode.getOffset(), 2);
    }

    class TestNode extends Node {

        int _phraseHash;
        int _offset;

        TestNode(Label label) {
            super(label);
        }

        @Override
        void associate(List<String> phrase, int offset) {
            _phraseHash = phrase.hashCode();
            _offset = offset;
        }

        int getPhraseHash() { return _phraseHash; }

        int getOffset() { return _offset; }
    }

    @Test
    public void markov_createTestMixin() {

        MarkovChain mc = new MarkovChain(_ORDER);
        TestMixin mixin = new TestMixin();
        mc.setMixin(mixin);
        List<String> train = new LinkedList<>(Arrays.asList("foo", "bar", "baz"));
        mc.train(train);

        List<String> phrase = new LinkedList<>(Arrays.asList("foo", "bar", "baz"));
        double result = mc.match(phrase);

        assertNotEquals(mixin._initId, 0);
        assertEquals(mixin._initId, mixin._updateId);
        assertEquals(mixin._updateId, mixin._finishId);
    }

    class TestMixin extends MarkovChainMixin {

        int _initId;
        int _updateId;
        int _finishId;

        @Override
        int initQuery(List<String> phrase) {
            _initId = super.initQuery(phrase);
            return _initId;
        }

        @Override
        void updateQuery(int id, Node node, Edge edge) {
            _updateId = id;
        }

        @Override
        void finishQuery(int id) {
            _finishId = id;
        }
    }
}
