package com.miniapp.account;

import android.util.Log;

/**
 * Created by zl on 19-10-25.
 * 打印日志工具
 * 当level=VERBOSE，日志全部打印
 * 当level=NOTHING，日志全部不打印
 */
public class LogUtil {
    private static final String TAG = "zl_Account_LogUtil";

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;

    private static int mLevel = VERBOSE;

    public static void setLogLevel(int level) {
        mLevel = level;
    }

    public static void v(String msg) {
        v(TAG, msg);
    }
    public static void v(String tag, String msg) {
        if (mLevel <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String msg) {
        d(TAG, msg);
    }
    public static void d(String tag, String msg) {
        if(mLevel <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        i(TAG, msg);
    }
    public static void i(String tag, String msg) {
        if(mLevel <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String msg) {
        w(TAG, msg);
    }
    public static void w(String tag, String msg) {
        if(mLevel <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String msg) {
        e(TAG, msg);
    }
    public static void e(String tag, String msg) {
        if(mLevel <= ERROR) {
            Log.e(tag, msg);
        }
    }
}