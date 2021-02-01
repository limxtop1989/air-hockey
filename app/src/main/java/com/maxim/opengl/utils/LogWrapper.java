package com.maxim.opengl.utils;

import android.util.Log;


import com.maxim.opengl.BuildConfig;

import java.security.InvalidParameterException;

/**
 * Debug version enable log automatically for all level.
 *
 * Release version enable log automatically above INFO level inclusively. To enable the specified
 * tag at the specified level, execute command: 'setprop log.tag.<YOUR_LOG_TAG> <LEVEL>', take AppLock
 * as an example: setprop log.tag.AppLock DEBUG. reboot the device to reset all your settings.
 *
 * See https://km.trasre.com/pages/viewpage.action?pageId=14402457 for detail.
 * @author miaoxin.li
 */
public class LogWrapper {
    private static final String LOG_TAG = "Research";

    private static final String CALLEE_NAME = LogWrapper.class.getCanonicalName();
    private static final int CALLEE_INIT     = 1;
    private static final int CALLEE_VISITING = 2;
    private static final int CALLEE_VISITED  = 3;
    private static final int CALLER_VISITED  = 4;

    private static final boolean IS_FULLY_QUALIFIED = false;

    /*
     * Always true no matter it is debug or release version.
     */
    private static boolean ERROR = isLoggable(Log.ERROR);
    private static boolean WARN = isLoggable(Log.WARN);
    private static boolean INFO = isLoggable(Log.INFO);

    // True when debug and false when release, in the meanwhile, the value can change to be true in
    // running time by execute command: setprop log.tag.<YOUR_LOG_TAG> <LEVEL>
    private static boolean DEBUG = isLoggable(Log.DEBUG);
    private static boolean VERBOSE = isLoggable(Log.VERBOSE);

    static boolean MOBILE = false;

    /**
     * Checks to see whether or not a log for the specified tag is loggable at the specified level.
     * The order in terms of priority, from highest to lowest is (or, from least to most) is
     * ERROR, WARN, INFO, DEBUG, VERBOSE.
     *
     * The default level of any tag is set to INFO. This means that any level above and including
     * INFO will be logged.  You can change the default level by setting a system property:
     * 'setprop log.tag.<YOUR_LOG_TAG> <LEVEL>' Where level is either VERBOSE, DEBUG, INFO, WARN,
     * ERROR, or ASSERT. You can also create a local.prop file that with the following in it:
     * 'log.tag.<YOUR_LOG_TAG>=<LEVEL>' and place that in /data/local.prop.
     * @param level
     * @return
     */
    private static boolean isLoggable(int level) {
        return BuildConfig.DEBUG || Log.isLoggable(LOG_TAG, level) || MOBILE;
    }

    public static void init() {
        ERROR = isLoggable(Log.ERROR);
        WARN = isLoggable(Log.WARN);
        INFO = isLoggable(Log.INFO);

        DEBUG = isLoggable(Log.DEBUG);
        VERBOSE = isLoggable(Log.VERBOSE);
    }

    /**
     * Always output the log.
     * @param className
     * @param methodName
     */
    public static void e(String className, String methodName) {
        e(className, methodName, null, null, null);
    }

    /**
     * Always output the log.
     * @param className
     * @param methodName
     * @param msg
     */
    public static void e(String className, String methodName, String msg) {
        e(className, methodName, msg, null, null);
    }

    /**
     * Always output the log.
     * @param className
     * @param methodName
     * @param msg
     */
    public static void e(String className, String methodName, String msg,  String[] names,
                         Object[] values) {
        if (ERROR) {
            Log.e(LOG_TAG, concat(className, methodName, msg, names, values));
        }
    }

    /**
     * Always output the log.
     * @param className
     * @param methodName
     */
    public static void w(String className, String methodName) {
        w(className, methodName, null, null, null);
    }

    /**
     * Always output the log.
     * @param className
     * @param methodName
     * @param msg
     */
    public static void w(String className, String methodName, String msg) {
        w(className, methodName, msg, null, null);
    }

    /**
     * Always output the log.
     * @param className
     * @param methodName
     * @param msg
     */
    public static void w(String className, String methodName, String msg, String[] names,
                         Object[] values) {
        if (WARN) {
            Log.w(LOG_TAG, concat(className, methodName, msg, names, values));
        }
    }

    /**
     * Always output the log.
     * @param className
     * @param methodName
     */
    public static void i(String className, String methodName) {
        i(className, methodName, null, null, null);
    }

    /**
     * Always output the log.
     * @param className
     * @param methodName
     * @param msg
     */
    public static void i(String className, String methodName, String msg) {
        i(className, methodName, msg, null, null);
    }

    /**
     * Always output the log.
     * @param className
     * @param methodName
     * @param msg
     * @param names
     * @param values
     */
    public static void i(String className, String methodName, String msg, String[] names,
                         Object[] values) {
        if (INFO) {
            Log.i(LOG_TAG, concat(className, methodName, msg, names, values));
        }
    }


    /**
     * Execute adb shell setprop log.tag.AppLock DEBUG to enable debug log.
     */
    public static void d() {
        d(null, null, null, false);
    }

    public static void d(boolean isPrintStack) {
        d(null, null, null, isPrintStack);
    }

    /**
     * Execute adb shell setprop log.tag.AppLock DEBUG to enable debug log.
     * @param message
     */
    public static void d(String message) {
        d(message, null, null, false);
    }

    /**
     * Execute adb shell setprop log.tag.AppLock DEBUG to enable debug log.
     * @param names
     * @param values
     */
    public static void d(String[] names, Object[] values) {
        d(null, names, values, false);
    }

    /**
     * Execute adb shell setprop log.tag.AppLock DEBUG to enable debug log.
     * @param message
     * @param names
     * @param values
     */
    public static void d(String message, String[] names, Object[] values) {
        d(message, names, values, false);
    }

    /**
     * Execute adb shell setprop log.tag.AppLock DEBUG to enable debug log.
     * @param msg
     * @param names
     * @param values
     */
    public static void d(String msg, String[] names, Object[] values, boolean isPrintStack) {
        if (!isLoggable(Log.DEBUG)) {
            return;
        }
        StackTraceElement callee = getCallerInfo(Thread.currentThread().getStackTrace(), isPrintStack);
        Log.d(LOG_TAG, concat(getClassName(callee), callee.getMethodName(), msg, names, values));
    }

    /**
     * Execute adb shell setprop log.tag.AppLock VERBOSE to enable debug log.
     */
    public static void v() {
        v(null, null, null);
    }

    /**
     * Execute adb shell setprop log.tag.AppLock VERBOSE to enable debug log.
     * @param msg
     */
    public static void v(String msg) {
        v(msg, null, null);
    }

    /**
     * Execute adb shell setprop log.tag.AppLock VERBOSE to enable debug log.
     * @param names
     * @param values
     */
    public static void v(String[] names, Object[] values) {
        v(null, names, values);
    }

    /**
     * Execute adb shell setprop log.tag.AppLock VERBOSE to enable debug log.
     * @param msg
     * @param names
     * @param values
     */
    public static void v(String msg, String[] names, Object[] values) {
        if (!isLoggable(Log.VERBOSE)) {
            return;
        }
        StackTraceElement callee = getCallerInfo(Thread.currentThread().getStackTrace(), false);
        Log.v(LOG_TAG, concat(getClassName(callee), callee.getMethodName(), msg, names, values));
    }

    /**
     * State machine
     * @param stackTraceElements
     * @return stack trace element of caller.
     */
    private static StackTraceElement getCallerInfo(StackTraceElement[] stackTraceElements,
                                                   boolean isPrintStack) {
        int calleeStatus = CALLEE_INIT;
        StackTraceElement target = null;
        for (StackTraceElement ste : stackTraceElements) {

            switch (calleeStatus) {
                case CALLEE_INIT:
                    if (ste.getClassName().contains(CALLEE_NAME)) {
                        calleeStatus = CALLEE_VISITING;
                    }
                    break;
                case CALLEE_VISITING:
                    if (!ste.getClassName().contains(CALLEE_NAME)) {
                        calleeStatus = CALLEE_VISITED;
                        target = ste;
                    }
                    break;
                case CALLEE_VISITED:
                    calleeStatus = CALLER_VISITED;
                    break;
            }

            if (calleeStatus < CALLEE_VISITED) {
                continue;
            }


            if (isPrintStack) {
                Log.d(LOG_TAG, "    " + ste.getClassName() + "." + ste.getMethodName());
            } else {
                break;
            }
        }

        if (null == target) {
            // Theoretically, this should be impossible.
            Log.e(LOG_TAG, "please contact miaoxin.li if you read this message");
        }
        return target;
    }

    private static String getClassName(StackTraceElement ste) {
        if (IS_FULLY_QUALIFIED) {
            return ste.getClassName();
        }

        String className = ste.getClassName();
        return className.substring(className.lastIndexOf('.') + 1);
    }

    private static String concat(String className, String methodName, String msg, String[] names,
                                 Object[] values) {
        StringBuilder sb = new StringBuilder(80);
        sb.append(className).append("_").append(methodName);
        if (null != msg) {
            sb.append("_").append(msg);
        }

        if (null == names || null == values ) {
            return sb.toString();
        }
        if (names.length != values.length) {
            throw new InvalidParameterException("Length of names and values array is not equal");
        }

        sb.append(" { ");
        int length = names.length;
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(names[i]).append("=").append(values[i]);
        }
        sb.append(" }");
        return sb.toString();
    }
}
