package com.foresthouse.dynamiccrawler.utils.js;

import android.webkit.JavascriptInterface;

public class DCJavascriptInterface {
    static String CrawledHtml = null;
    @JavascriptInterface
    public void getHtml(String html) {
        CrawledHtml = html;
    }
}
