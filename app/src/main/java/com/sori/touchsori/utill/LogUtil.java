package com.sori.touchsori.utill;

import android.util.Log;

public class LogUtil {
    // TODO 상용 (로그 사용 유무)
    private static boolean isEnable = true;     // 로그사용 플래그 (사용)
//    private static boolean isEnable = false;    // 로그사용 플래그 (미사용)

    // Information
    public static void i(String tag, String msg) { if (isEnable && msg != null) Log.i(tag, msg); }

    // Debug
    public static void d(String tag, String msg) { if (isEnable && msg != null) Log.d(tag, msg); }

    // Warning
    public static void w(String tag, String msg) {
        if (isEnable && msg != null) Log.w(tag, msg);
    }

    // Error
    public static void e(String tag, String msg) {
        if (isEnable && msg != null) Log.e(tag, msg);
    }

    // Verbose
    public static void v(String tag, String msg) {
        if (isEnable && msg != null) Log.v(tag, msg);
    }
}