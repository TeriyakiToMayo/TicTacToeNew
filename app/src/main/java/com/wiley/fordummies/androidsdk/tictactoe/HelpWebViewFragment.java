package com.wiley.fordummies.androidsdk.tictactoe;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Fragment that shows WebView for Tic-Tac-Toe on Wikipedia.
 *
 * Created by adamcchampion on 2017/08/14.
 */

public class HelpWebViewFragment extends Fragment implements View.OnClickListener {
    private String mUrl;
    private ProgressBar mProgressBar;

    private static final String ARG_URI = "url";
    private final String TAG = getClass().getSimpleName();

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help_webview, container, false);

        WebView helpInWebView = (WebView) v.findViewById(R.id.helpwithwebview);
        mProgressBar = (ProgressBar) v.findViewById(R.id.webviewprogress);
        mProgressBar.setMax(100);

        View buttonExit = v.findViewById(R.id.button_exit);
        buttonExit.setOnClickListener(this);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            mUrl = extras.getString(ARG_URI);
            Log.d(TAG, "Loading URL " + mUrl);
        }
        WebView.setWebContentsDebuggingEnabled(true);
        helpInWebView.getSettings().setJavaScriptEnabled(true);
        helpInWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        helpInWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int progress) {
                if (progress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
                else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(progress);
                }
            }
        });

        helpInWebView.loadUrl(mUrl);


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(getResources().getString(R.string.help_webview));
            }
        }
        catch (NullPointerException npe) {
            Log.e(TAG, "Could not set subtitle");
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_exit:
                getActivity().finishFromChild(getActivity());
                break;
        }
    }
}
