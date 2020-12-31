package com.foresthouse.dynamiccrawler.utils;

import android.os.Build;

public class Distinguisher {

    public static boolean isDigit(char obj){
        return isDigit(String.valueOf(obj));
    }
    public static boolean isDigit(String obj){
        try{
            Integer.parseInt(obj);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static int checkSDKVersion(){
        return Build.VERSION.SDK_INT;
    }
    

}
