package com.foresthouse.dynamiccrawler.utils.js;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.R;
import com.foresthouse.dynamiccrawler.utils.DataManager;
import com.foresthouse.dynamiccrawler.utils.Distinguisher;
import com.foresthouse.dynamiccrawler.utils.Generator;
import com.foresthouse.dynamiccrawler.utils.Waitable;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import static com.foresthouse.dynamiccrawler.ui.EditorActivity.crawler;
import static com.foresthouse.dynamiccrawler.utils.js.DCJavascriptInterface.CrawledHtml;

public class Api extends ScriptableObject {
    private static final String TAG = "{ JS Debug Log } >>>";

    private static final Api api = new Api();

    private static OnSuspendedListener suspendCommand;
    static void setOnInterruptedListener(OnSuspendedListener onSuspendedListener) { suspendCommand = onSuspendedListener; }

    private static JavaScriptEngineManager getJSEngineObj() {
        return JavaScriptEngineManager.JSEngineObjMap.get(Thread.currentThread().getName());
    }

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
        MainActivity.postAndWait(getJSEngineObj(), false, new Runnable() {
            @Override
            public void run() {
                Generator.makeToastMessage(MainActivity.ApplicationContext, msg);
            }
        });
    }
    @JSStaticFunction
    public static void makePopup(String title, String msg) {
        MainActivity.postAndWait(getJSEngineObj(), false, new Runnable() {
            @Override
            public void run() {
                Generator.makeYNDialog(MainActivity.ApplicationContext, title, msg, null, null, null, null, null, null, null);
            }
        });
    }

    // File Command
    @JSStaticFunction
    public static String getDefaultPath() {
        return MainActivity.ApplicationContext.getFilesDir().getPath();
    }

    @JSStaticFunction
    public static String getAbsolutePath() {
        if (Distinguisher.checkSDKVersion() <= Build.VERSION_CODES.P) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            MainActivity.MainHandler.post(() -> Generator
                    .makeNotifyDialog(MainActivity.ApplicationContext, "Warning", DataManager.getStringResources(R.string.str_deprecated_fileIO),
                                      DataManager.getStringResources(R.string.str_confirm)));
            suspend();
            return null;
        }
    }

    @JSStaticFunction
    public static void writeFile(String path, String fileName, String content) {
        DataManager.writeFile(path, fileName, content);
    }

    @JSStaticFunction
    public static String readFile(String path, String fileName) {
        return DataManager.readFile(path, fileName);
    }

    // View Property Command
    @JSStaticFunction
    public static void maximizeWindow() {
        MainActivity.postAndWait(getJSEngineObj(), false, new Runnable() {
            @Override
            public void run() {
                crawler.setVisibility(View.VISIBLE);
            }
        });
    }
    @JSStaticFunction
    public static void minimizeWindow() {
        MainActivity.postAndWait(getJSEngineObj(), false, new Runnable() {
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
        Waitable waitable = getJSEngineObj();
        MainActivity.postAndWait(waitable, false, new Runnable() {
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
                    crawler.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            try {
                                waitable.stopWaiting();
                            } catch (NullPointerException ignore) {}
                        }
                    });
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
        MainActivity.postAndWait(getJSEngineObj(), false, new Runnable() {
            @Override
            public void run() {
                crawler.getSettings().setJavaScriptEnabled(false);
            }
        });
    }
    @JSStaticFunction
    public static void loadUrl(String url) {
        MainActivity.postAndWait(getJSEngineObj(), false, new Runnable() {
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
    private String _getHtml() {
        MainActivity.postAndWait(getJSEngineObj(), false, new Runnable() {
            @Override
            public void run() {
                crawler.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
            }
        });
        for (int i = 0; i < Integer
                .parseInt(DataManager.RootPreference.getString("set_html_parse_timeout", "5")) * 100; i++) { // Default Timeout = 5s
            try {
                Thread.sleep(10);
                if (CrawledHtml != null) {
                    break;
                }
            } catch (InterruptedException ignore) {
            }
        }

        String source = CrawledHtml;
        CrawledHtml = null;
        return source;
    }
    @JSStaticFunction
    public static void execute(String command) {
        MainActivity.postAndWait(getJSEngineObj(), false, new Runnable() {
            @Override
            public void run() {
                crawler.loadUrl("javascript:(function() { document." + command + "; } )()");
            }
        });
    }

}
