package com.foresthouse.dynamiccrawler.utils.compiler;

public class UnknownCommandException extends Exception{
    protected UnknownCommandException(int num) {
        super("Unknown code type. You can only use 'GC Code' or 'Javascript'." +
                      "\nLine number : " + String.valueOf(num));
    }
}
