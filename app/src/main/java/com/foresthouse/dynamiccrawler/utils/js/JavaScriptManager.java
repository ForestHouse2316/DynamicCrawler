package com.foresthouse.dynamiccrawler.utils.js;

import android.util.Log;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.utils.Generator;

import org.jsoup.Jsoup;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.Arrays;


public class JavaScriptManager implements Runnable{ //TODO 백그라운드에서 실행하기
    private static final String TAG = "[ JavaScriptManager ]";

    private final String code;

    public JavaScriptManager(String code) {
        this.code = code;
    }

    @Override
    public void run() {
        runJS(code);
    }

    public static void runJS(String code) {
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
        }
    }

}
