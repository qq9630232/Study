package com.elita.studydemo;

import android.util.Log;

public class ElitaLogUtils {

    //默认打开日志
    private static boolean mIsEnableLog = true;

    /**
     * @param isEnableLog true开启 false关闭
     */
    public static void setEnableLog(boolean isEnableLog) {
        mIsEnableLog = isEnableLog;
    }

    public static void i(String tag, String msg) {
        if (!mIsEnableLog) {
            return;
        }
        Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (!mIsEnableLog) {
            return;
        }
        Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (!mIsEnableLog) {
            return;
        }
        Log.e(tag, msg);
    }

    public static void e(String msg) {
        if (!mIsEnableLog) {
            return;
        }
        Log.e("elita_lib", msg);
    }

    public static void i(String msg) {
        if (!mIsEnableLog) {
            return;
        }
        Log.i("elita_lib", msg);
    }

    public static void w(String msg) {
        if (!mIsEnableLog) {
            return;
        }
        Log.w("elita_lib", msg);
    }
}
