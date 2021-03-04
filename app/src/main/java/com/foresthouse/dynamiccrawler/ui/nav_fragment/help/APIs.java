package com.foresthouse.dynamiccrawler.ui.nav_fragment.help;

import android.os.Bundle;

import com.foresthouse.dynamiccrawler.ui.HtmlDocViewer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class APIs extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HtmlDocViewer.showHelp("APIs", this);
    }
}
