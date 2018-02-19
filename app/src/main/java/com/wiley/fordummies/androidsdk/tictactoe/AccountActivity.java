package com.wiley.fordummies.androidsdk.tictactoe;

import android.support.v4.app.Fragment;

/**
 * Activity for managing user accounts.
 *
 * Created by adamcchampion on 2017/08/05.
 */

public class AccountActivity extends SingleFragmentActivity {
    private final String TAG = getClass().getSimpleName();

    @Override
    protected Fragment createFragment() {
        return new AccountFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }
}
