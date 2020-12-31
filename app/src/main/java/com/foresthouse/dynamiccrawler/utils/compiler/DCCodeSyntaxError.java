package com.foresthouse.dynamiccrawler.utils.compiler;

public class DCCodeSyntaxError extends Exception{
    protected DCCodeSyntaxError(int num) {
        super("Syntax Error." +
                      "\nLine Number : " + num);
    }
}
