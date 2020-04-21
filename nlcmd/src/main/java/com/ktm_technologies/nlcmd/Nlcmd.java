package com.ktm_technologies.nlcmd;

import android.annotation.SuppressLint;

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
