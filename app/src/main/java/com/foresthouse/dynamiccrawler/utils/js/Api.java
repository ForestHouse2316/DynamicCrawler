package com.foresthouse.dynamiccrawler.utils.js;

import android.util.Log;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.utils.Generator;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

public class Api extends ScriptableObject { // TODO 여기 명령어들에게 핸들러 주기

    private static final String TAG = "{ JS Debug Log } >>>";

    @Override
    public String getClassName() {
        return "Api";
    }

    @JSStaticFunction
    public static String hello() {
        return "HELLO VELLO JELLO!";
    }
    @JSStaticFunction
    public static void log() {
        Log.d(TAG, "log: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>     JS LOG     >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
    @JSStaticFunction
    public static void makeToast(String msg) {
        Generator.makeToastMessage(MainActivity.ApplicationContext, msg);
    }
}
