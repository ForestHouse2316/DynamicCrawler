package com.foresthouse.dynamiccrawler.utils.js;

import android.util.Log;
import android.widget.Toast;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.utils.Generator;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;



public class JavaScriptManager { //TODO 백그라운드에서 실행하기
    private static final String TAG = "[ JavaScriptManager ]";

    public static void runJS(String code) {
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        try{
            // 스코프 지정
            Scriptable scope = rhino.initStandardObjects();
            Scriptable scope2 = rhino.initSafeStandardObjects();
            // Context 스코프 설정 (변수명 Context 로 앱 콘텍스트 사용 가능)
            ScriptableObject.putProperty(scope, "Context", MainActivity.ApplicationContext);

            // 커스텀 API 클래스를 라이노 엔진에 선언
            ScriptableObject.defineClass(scope, Api.class);


            rhino.evaluateString(scope, code, "JavaScript", 1, null);
        } catch (Exception e) {
            Generator.makeToastMessage(MainActivity.ApplicationContext, e.toString());
            Log.d(TAG, "runJS: "+e.toString());
        } finally {
            Context.exit();
        }
    }
}
