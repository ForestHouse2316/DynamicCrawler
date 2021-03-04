package com.foresthouse.dynamiccrawler.utils;

public class ThreadManager {

    public static Thread runInAnotherThread(Runnable r, boolean daemon) {
        Thread thread = new Thread(r);
        thread.setDaemon(daemon);
        thread.start();
        return thread;
    }
}
