package com.foresthouse.dynamiccrawler.utils.js;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.R;
import com.foresthouse.dynamiccrawler.utils.Generator;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import static com.foresthouse.dynamiccrawler.MainActivity.MainHandler;
import static com.foresthouse.dynamiccrawler.ui.EditorActivity.crawler;
import static com.foresthouse.dynamiccrawler.utils.js.DCJavascriptInterface.CrawledHtml;

public class Api extends ScriptableObject { // TODO 여기 명령어들에게 핸들러 주기
    private static final String TAG = "{ JS Debug Log } >>>";

    private static final Api api = new Api();

    private static OnSuspendedListener suspendCommand;
    static void setOnInterruptedListener(OnSuspendedListener onSuspendedListener) { suspendCommand = onSuspendedListener; }

    @Override
    public String getClassName() {
        return "Api";
    }









    // Debug Command
    @JSStaticFunction
    public static String hello() {
        return "FELLO HELLO!";
    }
    @JSStaticFunction
    public static void log() {
        Log.d(TAG, "log: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>     JS LOG     >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
    @JSStaticFunction
    public static void makeToast(String msg) {
        Generator.makeToastMessage(MainActivity.ApplicationContext, msg);
    }
    @JSStaticFunction
    public static void makePopup(String title, String msg) {
        Generator.makeYNDialog(MainActivity.ApplicationContext, title, msg,
                               null, null, null, null, null, null,null);
    }

    // View Property Command
    @JSStaticFunction
    public static void maximizeWindow() {
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                crawler.setVisibility(View.VISIBLE);
            }
        });
    }
    @JSStaticFunction
    public static void minimizeWindow() {
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                crawler.setVisibility(View.GONE);
            }
        });
    }

    // WebView Control Command
    @SuppressLint("SetJavaScriptEnabled")
    @JSStaticFunction
    public static void start() {
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (crawler == null) {
                    Generator.makeToastMessage(MainActivity.ApplicationContext, MainActivity.ApplicationContext.getString(R.string.str_failed_to_load));
//                    throw new NullPointerException();
                    suspend();
                } else {
                    crawler.getSettings().setJavaScriptEnabled(true);
                    crawler.getSettings().setUseWideViewPort(true);
                    crawler.getSettings().setDomStorageEnabled(true);
                    crawler.getSettings().setAppCacheEnabled(true);
                    crawler.getSettings().setSupportZoom(true);
                    crawler.getSettings().setBuiltInZoomControls(true);
                    crawler.getSettings().setDisplayZoomControls(false);
                    crawler.getSettings().setSaveFormData(true);
                    crawler.getSettings().setSupportMultipleWindows(false); // true : Open window in default browser
                    crawler.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    crawler.addJavascriptInterface(new DCJavascriptInterface(), "Android");
                    crawler.setWebChromeClient(new WebChromeClient());
                    //TODO loadUrl 하면 다음 코드 실행 전에 로드 다 될 때까지 기다리는거 만들기
                }
            }
        });

    }
    @JSStaticFunction
    public static void suspend() {
        minimizeWindow();
        suspendCommand.onSuspended();
    }
    @JSStaticFunction
    public static void disableJS() {
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                crawler.getSettings().setJavaScriptEnabled(false);
            }
        });
    }
    @JSStaticFunction
    public static void loadUrl(String url) {
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                crawler.loadUrl(url);
            }
        });
    }
    @JSStaticFunction
    public static String getHtml() {
        return api._getHtml();
    }
    private synchronized String _getHtml() {
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                crawler.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
            }
        });
        String source = CrawledHtml;
        CrawledHtml = null;
        while(source != null) {
            try {
                wait();
            } catch (InterruptedException ignore) { }
        }
        return source;
    }
    @JSStaticFunction
    public static void execute(String command) { crawler.loadUrl("javascript:(function() { document." + command + "; } )()"); }

}
