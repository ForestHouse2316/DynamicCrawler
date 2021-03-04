package com.foresthouse.dynamiccrawler.ui.nav_fragment.help;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.foresthouse.dynamiccrawler.ui.HtmlDocViewer;

import androidx.appcompat.app.AppCompatActivity;

public class BasicGuide extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtmlDocViewer.showHelp("BasicGuide", this);
    }
}