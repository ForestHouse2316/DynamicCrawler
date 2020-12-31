package com.foresthouse.dynamiccrawler;

public class JavaLaboratory {
    public static void main(String[] args) {
        Object b = new B();
        System.out.println(b instanceof A);
    }
}
class A {}
class B extends A {}
