package com.wiley.fordummies.androidsdk.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

/**
 * Fragment for login screen.
 *
 * Created by adamcchampion on 2017/08/03.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private AccountSingleton mAccountSingleton;
    private AccountDbHelper mDbHelper;

    private final static String OPT_NAME = "name";
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            v = inflater.inflate(R.layout.fragment_login_land, container, false);
        } else {
            v = inflater.inflate(R.layout.fragment_login, container, false);
        }

        mUsernameEditText = (EditText) v.findViewById(R.id.username_text);
        mPasswordEditText = (EditText) v.findViewById(R.id.password_text);

        Button loginButton = (Button) v.findViewById(R.id.login_button);
        if (loginButton != null) {
            loginButton.setOnClickListener(this);
        }
        Button cancelButton = (Button) v.findViewById(R.id.cancel_button);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(this);
        }
        Button newUserButton = (Button) v.findViewById(R.id.new_user_button);
        if (newUserButton != null) {
            newUserButton.setOnClickListener(this);
        }

        return v;
    }

    private void checkLogin() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (mAccountSingleton == null) {
            mAccountSingleton = AccountSingleton.get(getActivity().getApplicationContext());
        }

        if (mDbHelper == null) {
            mDbHelper = new AccountDbHelper(getActivity().getApplicationContext());
        }
        List<Account> accountList = mAccountSingleton.getAccounts();
        boolean hasMatchingAccount = false;
        for (Account account: accountList) {
            if (account.getName().equals(username) && account.getPassword().equals(password)) {
                hasMatchingAccount = true;
                break;
            }
        }

        if (accountList.size() > 0 && hasMatchingAccount) {
            // Save username as the name of the player
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(OPT_NAME, username);
            editor.apply();

            // Bring up the GameOptions screen
            startActivity(new Intent(getActivity(), GameOptionsActivity.class));
            getActivity().finish();
        }
        else {
            FragmentManager manager = getFragmentManager();
            LoginErrorDialogFragment fragment = new LoginErrorDialogFragment();
            fragment.show(manager, "login_error");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                checkLogin();
                break;
            case R.id.cancel_button:
                getActivity().finish();
                break;
            case R.id.new_user_button:
                int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new AccountFragment();
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                    //startActivity(new Intent(getActivity().getApplicationContext(), AccountActivity.class));
                    fm.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack("account_fragment")
                            .commit();
                }
                else {
                    fm.beginTransaction()
                            .add(R.id.account_fragment_container, fragment)
                            .addToBackStack("account_fragment")
                            .commit();
                }
                break;
        }
    }
}
