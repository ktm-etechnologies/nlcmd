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

import android.annotation.SuppressLint;

import java.util.List;

/**
 * This class provides auxiliary functions such as logging.
 * TODO Actually use java logger instead of System.out
 */
@SuppressWarnings("WeakerAccess")
public class Nlcmd {

    public static final int LEVEL_ERROR = 0;
    public static final int LEVEL_WARNING = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_DEBUG = 3;
    public static final int LEVEL_VERBOSE = 4;

    private static int _level = LEVEL_VERBOSE;
    private static int _order = 2;
    private static ScoreMode _scoreMode = ScoreMode.LONGEST_AVG_REL;
    private static CommandSet _cs = null;

    /**
     * @return Score mode, see {@link ScoreMode}
     */
    public static ScoreMode getScoreMode() {
        return _scoreMode;
    }

    /**
     * @param scoreMode Score mode, see {@link ScoreMode}
     */
    public static void setScoreMode(ScoreMode scoreMode) throws IllegalStateException {
        if (_cs != null) {
            throw new IllegalStateException("Can not set score mode after actions have been registered");
        }
        _scoreMode = scoreMode;
    }
    /**
     * @return Order for markov chains, that is number of relevant previous steps when matching
     */
    public static int getOrder() {
        return _order;
    }

    /**
     * @param order Order for markov chains, that is number of relevant previous steps when matching
     * @throws IllegalStateException
     */
    public static void setOrder(int order) throws IllegalStateException {
        if (_cs != null) {
            throw new IllegalStateException("Can not set order after actions have been registered");
        }
        if (order < 1) {
            throw new IndexOutOfBoundsException("MarkovChain order size can not be < 1");
        }

        _order = order;
    }

    /**
     * Unregister all existing actions.
     */
    public static void reset() {
        _cs = null;
    }

    /**
     *
     * @param phrases
     * @param callback
     */
    public static void action(String[] phrases, ActionLambda callback) {

        // Lazy instantiation to respec _order and _scoreMode
        if (_cs == null) {
            _cs = new CommandSet(_order, _scoreMode);
        }

        _cs.put(callback, phrases);
    }

    /**
     * Match input phrase against actions and run the best fitting one.
     * The #match() method only considers full lengths input phrase covering models.
     * @param phrase Input phrase
     * @see Nlcmd#scan(List) for sub-phrase matching
     */
    public static void match(List<String> phrase) {

        if (_cs == null) {
            // No actions registered
            return;
        }

        Object object = _cs.match(phrase);
        if (object instanceof ActionLambda) {
            ActionLambda callback = (ActionLambda)object;
            callback.run();
        }
    }

    /**
     * Match input phrase against actions and run the best fitting one.
     * The #scan() method
     * @param phrase Input phrase
     * @see Nlcmd#match(List) for strict full-phrase matching only
     */
    public static void scan(List<String> phrase) {

        if (_cs == null) {
            // No actions registered
            return;
        }

        Object object = _cs.match(phrase);
        if (object instanceof ActionLambda) {
            ActionLambda callback = (ActionLambda)object;
            callback.run();
        }
    }

    /**
     * {@link android.util.Log#e}
     * @param tag Log entry prefix
     * @param d Numeric value
     */
    @SuppressLint("DefaultLocale")
    static void e(String tag, double d) {
        if (_level >= Nlcmd.LEVEL_ERROR) {
            System.out.println(tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#e}
     * @param tag Log entry prefix
     * @param msg String value
     */
    static void e(String tag, String msg) {
        if (_level >= Nlcmd.LEVEL_ERROR) {
            System.out.println(tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#w}
     * @param tag Log entry prefix
     * @param d Numeric value
     */
    @SuppressLint("DefaultLocale")
    static void w(String tag, double d) {
        if (_level >= Nlcmd.LEVEL_WARNING) {
            System.out.println(tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#w}
     * @param tag Log entry prefix
     * @param msg String value
     */
    static void w(String tag, String msg) {
        if (_level >= Nlcmd.LEVEL_WARNING) {
            System.out.println(tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#i}
     * @param tag Log entry prefix
     * @param d Numeric value
     */
    @SuppressLint("DefaultLocale")
    static void i(String tag, double d) {
        if (_level >= Nlcmd.LEVEL_INFO) {
            System.out.println(tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#i}
     * @param tag Log entry prefix
     * @param msg String value
     */
    static void i(String tag, String msg) {
        if (_level >= Nlcmd.LEVEL_INFO) {
            System.out.println(tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#d}
     * @param tag Log entry prefix
     * @param d Numeric value
     */
    @SuppressLint("DefaultLocale")
    static void d(String tag, double d) {
        if (_level >= Nlcmd.LEVEL_DEBUG) {
            System.out.println(tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#d}
     * @param tag Log entry prefix
     * @param msg String value
     */
    static void d(String tag, String msg) {
        if (_level >= Nlcmd.LEVEL_DEBUG) {
            System.out.println(tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#v}
     * @param tag Log entry prefix
     * @param d Numeric value
     */
    @SuppressLint("DefaultLocale")
    static void v(String tag, double d) {
        if (_level >= Nlcmd.LEVEL_VERBOSE) {
            System.out.println(tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#v}
     * @param tag Log entry prefix
     * @param msg String value
     */
    static void v(String tag, String msg) {
        if (_level >= Nlcmd.LEVEL_VERBOSE) {
            System.out.println(tag + ": " + msg);
        }
    }
}
