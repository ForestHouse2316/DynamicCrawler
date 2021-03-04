package com.foresthouse.dynamiccrawler.ui.nav_fragment.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.foresthouse.dynamiccrawler.ui.HtmlDocViewer;

import androidx.appcompat.app.AppCompatActivity;

public class PatchNote extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtmlDocViewer.showHtml("PatchNote", this);
    }
}