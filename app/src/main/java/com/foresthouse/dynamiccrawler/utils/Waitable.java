package com.foresthouse.dynamiccrawler.utils;

public interface Waitable {
    // boolean wait 을 선언하는 것을 기본 스타일로 함
    boolean WAIT = true;
    boolean RESUME = false;
    default void startWaiting() {
        startWaiting(0);
    }
    void startWaiting(int interval);
    void stopWaiting();
}
