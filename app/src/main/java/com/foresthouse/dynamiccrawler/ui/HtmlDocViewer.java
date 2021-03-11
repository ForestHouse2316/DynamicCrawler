package com.foresthouse.dynamiccrawler.ui;

import android.annotation.SuppressLint;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.foresthouse.dynamiccrawler.R;
import com.foresthouse.dynamiccrawler.utils.DataManager;

import androidx.appcompat.app.AppCompatActivity;

public class HtmlDocViewer {

    public static void showHelp(String name, AppCompatActivity activity) {
        showHtml("help/" + DataManager.getStringResource(R.string.env_language_code) + "/" + name, activity);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void showHtml(String name, AppCompatActivity activity) {
        activity.setContentView(R.layout.activity_html_doc_viewer);
        WebView viewer = activity.findViewById(R.id.wv_html_doc_viewer);
        viewer.setWebViewClient(new WebViewClient());
        viewer.getSettings().setJavaScriptEnabled(true);
        viewer.loadUrl("file:///android_asset/" + name + ".html");
    }
}
