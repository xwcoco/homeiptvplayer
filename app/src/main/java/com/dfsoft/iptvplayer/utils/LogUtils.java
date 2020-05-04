package com.dfsoft.iptvplayer.utils;

import android.util.Log;

public class LogUtils {
    private static final boolean debug =true;

    public static void i(String tag, String msg) {
        if (debug)
            Log.i(tag, msg);
    }

    public static void i(String msg) {
        if (debug) Log.i("vlc", msg);
    }
}
