package com.foresthouse.dynamiccrawler.utils;

import android.os.Build;

import java.util.Locale;

public class Distinguisher {

    public static boolean isDigit(char obj) {
        return isDigit(String.valueOf(obj));
    }

    public static boolean isDigit(String obj) {
        try {
            Integer.parseInt(obj);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int checkSDKVersion() {
        return Build.VERSION.SDK_INT;
    } //TODO Return 값을 Boolean 으로 하여 바로바로 체킹 가능하게 하기

    public static String getDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

}
