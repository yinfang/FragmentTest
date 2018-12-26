package com.clubank.util;

import android.util.Log;

import com.clubank.common.BuildConfig;


/**
 * Created by long on 17-7-26.
 */

public class Logger {

    private static boolean LOG_ENABLE = BuildConfig.DEBUG;

    public static void d(String TAG, String msg) {
        if (LOG_ENABLE) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String TAG, String msg, Throwable t) {
        if (LOG_ENABLE) {
            Log.d(TAG, msg, t);
        }
    }

    public static void i(String TAG, String msg) {
        if (LOG_ENABLE) {
            Log.i(TAG, msg);
        }
    }

    public static void i(String TAG, String msg, Throwable t) {
        if (LOG_ENABLE) {
            Log.i(TAG, msg, t);
        }
    }

    public static void e(String TAG, String msg) {
        if (LOG_ENABLE) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String TAG, String msg, Throwable t) {
        if (LOG_ENABLE) {
            Log.e(TAG, msg, t);
        }
    }

    public static void v(String TAG, String msg) {
        if (LOG_ENABLE) {
            Log.v(TAG, msg);
        }
    }

    public static void v(String TAG, String msg, Throwable t) {
        if (LOG_ENABLE) {
            Log.v(TAG, msg, t);
        }
    }

    public static void w(String TAG, String msg) {
        if (LOG_ENABLE) {
            Log.w(TAG, msg);
        }
    }

    public static void w(String TAG, Throwable t) {
        if (LOG_ENABLE) {
            Log.w(TAG, t);
        }
    }

    public static void w(String TAG, String msg, Throwable t) {
        if (LOG_ENABLE) {
            Log.w(TAG, msg, t);
        }
    }


    public static void wtf(String TAG, String msg) {
        if (LOG_ENABLE) {
            Log.wtf(TAG, msg);
        }
    }

    public static void wtf(String TAG, Throwable t) {
        if (LOG_ENABLE) {
            Log.wtf(TAG, t);
        }
    }

    public static void wtf(String TAG, String msg, Throwable t) {
        if (LOG_ENABLE) {
            Log.wtf(TAG, msg, t);
        }
    }

    public static String getStackTraceString(Throwable t) {
        if (LOG_ENABLE) {
            return Log.getStackTraceString(t);
        } else {
            return "当前处在非debug模式";
        }
    }

    public static void println(int priority, String tag, String msg) {
        if (LOG_ENABLE) {
            Log.println(priority, tag, msg);
        }
    }
}
