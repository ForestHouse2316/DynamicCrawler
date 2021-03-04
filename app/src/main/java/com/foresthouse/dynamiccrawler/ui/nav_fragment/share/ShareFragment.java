package com.foresthouse.dynamiccrawler.ui.nav_fragment.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foresthouse.dynamiccrawler.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ShareFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        return root;
    }
}