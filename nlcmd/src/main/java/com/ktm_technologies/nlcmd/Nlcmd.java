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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * This class provides static global command matching and execution callback with the #action()
 * method as well as related settings.
 *
 * For internal use there are auxiliary functions such as logging.
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
     * Expose only static API, no instantiation.
     */
    private Nlcmd() {}

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
     * @throws IllegalStateException If actions have already been registered, it's not possible to
     *                               use this method any more.
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
     * Register action with match phrases and hook
     * @param phrases Training phrases to build a markov chain for this action
     * @param callback Hook to run when the action is activated
     */
    public static void action(String[] phrases, MatchLambda callback) {

        // Lazy instantiation to respec _order and _scoreMode
        if (_cs == null) {
            _cs = new CommandSet(_order, _scoreMode, Locale.getDefault());
        }

        _cs.put(callback, phrases);
    }

    /**
     * Register detailed action with match phrases and hook
     * @param phrases Training phrases to build a markov chain for this action
     * @param callback Hook to run when the action is activated
     */
    public static void action(String[] phrases, ScanLambda callback) {

        // Lazy instantiation to respec _order and _scoreMode
        if (_cs == null) {
            _cs = new CommandSet(_order, _scoreMode, Locale.getDefault());
        }

        _cs.put(callback, phrases);
    }

    /**
     * Match input phrase against actions and run the best fitting one.
     * The #match() method only considers full lengths input phrase covering models.
     * @param phrase Input phrase
     * @see Nlcmd#scan(String) for sub-phrase matching
     */
    public static void match(String phrase) {

        List<String> list = Utils.words(phrase, Locale.getDefault());
        match(list);
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
        if (object instanceof MatchLambda) {
            MatchLambda callback = (MatchLambda)object;
            callback.run();
        } else if (object != null) {
            throw new ClassCastException("Can only use ActionLambda with match(). See also ActionDetailsLambda.");
        }
    }

    /**
     * Match input phrase against actions and run the best fitting one.
     * The #scan() method
     * @param phrase Input phrase
     * @see Nlcmd#match(String) for strict full-phrase matching only
     */
    public static void scan(String phrase) {

        List<String> list = Utils.words(phrase, Locale.getDefault());
        scan(list);
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

        HashMap<List<String>, Double> matches = new HashMap<>();
        HashMap<String, List<String>> placeholders = new HashMap<>();
        Object object = _cs.scan(phrase, matches, placeholders);
        if (object instanceof ScanLambda) {
            ScanLambda callback = (ScanLambda)object;
            callback.run(matches, placeholders);
        } else if (object != null) {
            throw new ClassCastException("Can only use ActionDetailsLambda with scan(). See also ActionLambda.");
        }
    }

    /**
     * {@link android.util.Log#e}
     * @param cls Class that's writing the entry
     * @param tag Log entry prefix
     * @param d Numeric value
     */
    @SuppressLint("DefaultLocale")
    static void e(Class cls, String tag, double d) {
        if (_level >= Nlcmd.LEVEL_ERROR) {
            System.out.println(cls.getSimpleName() + " " + tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#e}
     * @param cls Class that's writing the entry
     * @param tag Log entry prefix
     * @param msg String value
     */
    static void e(Class cls, String tag, String msg) {
        if (_level >= Nlcmd.LEVEL_ERROR) {
            System.out.println(cls.getSimpleName() + " " + tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#w}
     * @param cls Class that's writing the entry
     * @param tag Log entry prefix
     * @param d Numeric value
     */
    @SuppressLint("DefaultLocale")
    static void w(Class cls, String tag, double d) {
        if (_level >= Nlcmd.LEVEL_WARNING) {
            System.out.println(cls.getSimpleName() + " " + tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#w}
     * @param cls Class that's writing the entry
     * @param tag Log entry prefix
     * @param msg String value
     */
    static void w(Class cls, String tag, String msg) {
        if (_level >= Nlcmd.LEVEL_WARNING) {
            System.out.println(cls.getSimpleName() + " " + tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#i}
     * @param cls Class that's writing the entry
     * @param tag Log entry prefix
     * @param d Numeric value
     */
    @SuppressLint("DefaultLocale")
    static void i(Class cls, String tag, double d) {
        if (_level >= Nlcmd.LEVEL_INFO) {
            System.out.println(cls.getSimpleName() + " " + tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#i}
     * @param cls Class that's writing the entry
     * @param tag Log entry prefix
     * @param msg String value
     */
    static void i(Class cls, String tag, String msg) {
        if (_level >= Nlcmd.LEVEL_INFO) {
            System.out.println(cls.getSimpleName() + " " + tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#d}
     * @param cls Class that's writing the entry
     * @param tag Log entry prefix
     * @param d Numeric value
     */
    @SuppressLint("DefaultLocale")
    static void d(Class cls, String tag, double d) {
        if (_level >= Nlcmd.LEVEL_DEBUG) {
            System.out.println(cls.getSimpleName() + " " + tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#d}
     * @param cls Class that's writing the entry
     * @param tag Log entry prefix
     * @param msg String value
     */
    static void d(Class cls, String tag, String msg) {
        if (_level >= Nlcmd.LEVEL_DEBUG) {
            System.out.println(cls.getSimpleName() + " " + tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#v}
     * @param cls Class that's writing the entry
     * @param tag Log entry prefix
     * @param d Numeric value
     */
    @SuppressLint("DefaultLocale")
    static void v(Class cls, String tag, double d) {
        if (_level >= Nlcmd.LEVEL_VERBOSE) {
            System.out.println(cls.getSimpleName() + " " + tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#v}
     * @param cls Class that's writing the entry
     * @param tag Log entry prefix
     * @param msg String value
     */
    static void v(Class cls, String tag, String msg) {
        if (_level >= Nlcmd.LEVEL_VERBOSE) {
            System.out.println(cls.getSimpleName() + " " + tag + ": " + msg);
        }
    }
}
