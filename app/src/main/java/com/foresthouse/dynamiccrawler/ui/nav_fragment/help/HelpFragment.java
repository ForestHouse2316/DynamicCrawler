package com.foresthouse.dynamiccrawler.ui.nav_fragment.help;

import android.os.Bundle;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.R;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

public class HelpFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.CurrentFragment = MainActivity.FRAGMENT_HELP;
        addPreferencesFromResource(R.xml.help_preferences);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
