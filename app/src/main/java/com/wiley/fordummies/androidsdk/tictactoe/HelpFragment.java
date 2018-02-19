package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by adamcchampion on 2017/08/14.
 */

public class HelpFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);

        Button btOK = (Button) v.findViewById(R.id.button_help_ok);
        btOK.setOnClickListener(this);
        Button wikipedia = (Button) v.findViewById(R.id.button_lookup_wikipedia);
        wikipedia.setOnClickListener(this);
        Button wikipediaWebView = (Button) v.findViewById(R.id.button_lookup_wikipedia_in_web_view);
        wikipediaWebView.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(getResources().getString(R.string.help));
            }
        }
        catch (NullPointerException npe) {
            Log.e(TAG, "Could not set subtitle");
        }
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnected = true;
        boolean isWifiAvailable = networkInfo.isAvailable();
        boolean isWifiConnected = networkInfo.isConnected();
        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileAvailable = networkInfo.isAvailable();
        boolean isMobileConnnected = networkInfo.isConnected();
        isConnected = (isMobileAvailable && isMobileConnnected) || (isWifiAvailable && isWifiConnected);
        return (isConnected);
    }

    private void launchBrowser(String url) {
        Uri theUri = Uri.parse(url);
        Intent LaunchBrowserIntent = new Intent(Intent.ACTION_VIEW, theUri);
        startActivity(LaunchBrowserIntent);
    }

    private void launchWebView(String url) {
        Intent launchWebViewIntent = new Intent(getActivity().getApplicationContext(), HelpWebViewActivity.class);
        launchWebViewIntent.putExtra("url", url);
        startActivity(launchWebViewIntent);
    }

    private void noNetworkConnectionNotify() {
        FragmentManager manager = getFragmentManager();
        NoNetworkConnectionDialogFragment fragment = new NoNetworkConnectionDialogFragment();
        fragment.show(manager, "no_net_conn");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_help_ok:
                getActivity().finishFromChild(getActivity());
                break;
            case R.id.button_lookup_wikipedia:
                if (hasNetworkConnection()) {
                    launchBrowser("https://en.wikipedia.org/wiki/Tic-tac-toe");
                } else {
                    noNetworkConnectionNotify();
                }
                break;
            case R.id.button_lookup_wikipedia_in_web_view:
                if (hasNetworkConnection()) {
                    launchWebView("https://en.wikipedia.org/wiki/Tic-tac-toe");
                } else {
                    noNetworkConnectionNotify();
                }
                break;
        }
    }
}
