package com.wiley.fordummies.androidsdk.tictactoe;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by adamcchampion on 2017/08/13.
 */

public class SettingsActivity extends AppCompatActivity {

    protected Fragment createFragment() {
        return new SettingsFragment();
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            Fragment preferenceFragment = createFragment();
            fm.beginTransaction()
                    .replace(R.id.fragment_container, preferenceFragment)
                    .commit();
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

}
