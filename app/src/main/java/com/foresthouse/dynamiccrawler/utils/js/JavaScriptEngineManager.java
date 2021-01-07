package com.foresthouse.dynamiccrawler.utils.js;

import android.util.Log;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.utils.Generator;
import com.foresthouse.dynamiccrawler.utils.Waitable;

import org.jsoup.Jsoup;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.Arrays;
import java.util.HashMap;


public class JavaScriptEngineManager implements Runnable, Waitable {
    private static final String TAG = "[ JavaScriptEngineManager ]";

//    JavaScriptEngineManager JSEngineObj;
//    Thread JSEngineThread = null;
    public static HashMap<String, JavaScriptEngineManager> JSEngineObjMap = new HashMap<>();
    boolean wait;
    private final String code;

    public JavaScriptEngineManager(String code) {
        this.code = code;
        wait = false;
    }

    public void startEngineInBackground() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        JSEngineObjMap.put(thread.getName(), this);
        thread.start();
    }

    @Override
    public void run() {
        runJS(code);
    }

    public void runJS(String code) {
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        try{
            // 스코프 지정
            Scriptable scope = rhino.initStandardObjects();
            // 전역변수 설정
            //            ScriptableObject.putProperty(scope, "Context", MainActivity.ApplicationContext); // 보안문제로 인하여 콘텍스트 제공안함
            ScriptableObject.putProperty(scope, "Jsoup", Jsoup.class);

            // 커스텀 API 클래스를 라이노 엔진에 선언
            ScriptableObject.defineClass(scope, Api.class);

            rhino.evaluateString(scope, code, "JavaScript", 1, null);
            Api.setOnInterruptedListener(new OnSuspendedListener() {
                @Override
                public void onSuspended() {
                    try {
                        Context.exit();
                        JSEngineObjMap.remove(Thread.currentThread().getName());
                    } catch (IllegalStateException ignore) {}
                }
            });

        } catch (Exception e) {
            MainActivity.MainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Generator.makeToastMessage(MainActivity.ApplicationContext, e.toString());
                }
            });
            Log.d(TAG, "runJS: "+e.toString());
            Log.d(TAG, "runJS: " + Arrays.toString(e.getStackTrace()));
        } finally {
            Context.exit();
            JSEngineObjMap.remove(Thread.currentThread().getName());
        }
    }

    @Override
    public void startWaiting(int interval) {
        wait = WAIT;
        while (wait == WAIT) {
            if (interval > 0) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void stopWaiting() {
        wait = RESUME;
    }
}
