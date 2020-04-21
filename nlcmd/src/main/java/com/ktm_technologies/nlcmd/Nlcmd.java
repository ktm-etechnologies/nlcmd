package com.ktm_technologies.nlcmd;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides auxiliary functions such as logging.
 */
public class Nlcmd {

    private static Logger _logger = Logger.getLogger("org.ktm_technologies.nlcmd");
    static {
        _logger.setLevel(Level.FINEST);
        _logger.setUseParentHandlers(false);
        _logger.addHandler(new java.util.logging.ConsoleHandler());
    }

    /**
     * {@link android.util.Log#e}
     * @param tag
     * @param d
     */
    static void e(String tag, double d) {
        if (_logger.getLevel().intValue() <= Level.SEVERE.intValue()) {
            _logger.fine(tag + ": " + String.format("%f", d));
            System.out.println(tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#e}
     * @param tag
     * @param d
     */
    static void e(String tag, String msg) {
        if (_logger.getLevel().intValue() <= Level.SEVERE.intValue()) {
            _logger.fine(tag + ": " + msg);
            System.out.println(tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#w}
     * @param tag
     * @param d
     */
    static void w(String tag, double d) {
        if (_logger.getLevel().intValue() <= Level.WARNING.intValue()) {
            _logger.fine(tag + ": " + String.format("%f", d));
            System.out.println(tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#w}
     * @param tag
     * @param d
     */
    static void w(String tag, String msg) {
        if (_logger.getLevel().intValue() <= Level.WARNING.intValue()) {
            _logger.fine(tag + ": " + msg);
            System.out.println(tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#i}
     * @param tag
     * @param d
     */
    static void i(String tag, double d) {
        if (_logger.getLevel().intValue() <= Level.FINE.intValue()) {
            _logger.fine(tag + ": " + String.format("%f", d));
            System.out.println(tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#i}
     * @param tag
     * @param d
     */
    static void i(String tag, String msg) {
        if (_logger.getLevel().intValue() <= Level.FINE.intValue()) {
            _logger.fine(tag + ": " + msg);
            System.out.println(tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#d}
     * @param tag
     * @param d
     */
    static void d(String tag, double d) {
        if (_logger.getLevel().intValue() <= Level.FINER.intValue()) {
            _logger.fine(tag + ": " + String.format("%f", d));
            System.out.println(tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#d}
     * @param tag
     * @param d
     */
    static void d(String tag, String msg) {
        if (_logger.getLevel().intValue() <= Level.FINER.intValue()) {
            _logger.fine(tag + ": " + msg);
            System.out.println(tag + ": " + msg);
        }
    }

    /**
     * {@link android.util.Log#v}
     * @param tag
     * @param d
     */
    static void v(String tag, double d) {
        if (_logger.getLevel().intValue() <= Level.FINEST.intValue()) {
            _logger.fine(tag + ": " + String.format("%f", d));
            System.out.println(tag + ": " + String.format("%f", d));
        }
    }

    /**
     * {@link android.util.Log#v}
     * @param tag
     * @param d
     */
    static void v(String tag, String msg) {
        if (_logger.getLevel().intValue() <= Level.FINEST.intValue()) {
            _logger.fine(tag + ": " + msg);
            System.out.println(tag + ": " + msg);
        }
    }
}
