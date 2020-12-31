 package com.foresthouse.dynamiccrawler.utils.js;

import android.util.Log;

import com.foresthouse.dynamiccrawler.utils.Generator;

import java.util.Arrays;

public class test implements SecureAccessable {
    public static void pub() {
        Log.d("TAG", "pub: >>>>>>>>>>>>>>>>>>>>>>>");
        StackTraceElement[] stack;
        stack = new Throwable().getStackTrace();
        Log.d("TAG", "pub: "+ Arrays.toString(stack));
    } //TODO 2번째꺼가 호출메서드 이름임.

}
