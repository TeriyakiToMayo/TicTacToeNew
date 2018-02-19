package com.wiley.fordummies.androidsdk.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Fragment that handles main user navigation in the app.
 *
 * Created by adamcchampion on 2017/08/05.
 */

public class GameOptionsFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_options, container, false);

        Button btnNewGame = (Button) v.findViewById(R.id.buttonNewGame);
        btnNewGame.setOnClickListener(this);
        Button btnAudio = (Button) v.findViewById(R.id.buttonAudio);
        btnAudio.setOnClickListener(this);
        Button btnVideo = (Button) v.findViewById(R.id.buttonVideo);
        btnVideo.setOnClickListener(this);
        Button btnImage = (Button) v.findViewById(R.id.buttonImages);
        btnImage.setOnClickListener(this);
        Button btnMaps = (Button) v.findViewById(R.id.buttonMaps);
        btnMaps.setOnClickListener(this);
        Button btnSettings = (Button) v.findViewById(R.id.buttonSettings);
        btnSettings.setOnClickListener(this);
        Button btnHelp = (Button) v.findViewById(R.id.buttonHelp);
        btnHelp.setOnClickListener(this);
        Button btnTestSensors = (Button) v.findViewById(R.id.buttonSensors);
        btnTestSensors.setOnClickListener(this);
        Button btnExit = (Button) v.findViewById(R.id.buttonExit);
        btnExit.setOnClickListener(this);

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(getResources().getString(R.string.options));
            }
        }
        catch (NullPointerException npe) {
            Log.e(TAG, "Could not set subtitle");
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(getActivity().getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.menu_help:
                startActivity(new Intent(getActivity().getApplicationContext(), HelpActivity.class));
                return true;
            case R.id.menu_exit:
                showQuitAppDialog();
                return true;
            case R.id.menu_contacts:
                startActivity(new Intent(getActivity().getApplicationContext(), ContactsActivity.class));
                return true;
        }
        return false;
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNewGame:
                startActivity(new Intent(getActivity().getApplicationContext(), GameSessionActivity.class));
                break;
            case R.id.buttonAudio:
                startActivity(new Intent(getActivity().getApplicationContext(), AudioActivity.class));
                break;
            case R.id.buttonVideo:
                startActivity(new Intent(getActivity().getApplicationContext(), VideoActivity.class));
                break;
            case R.id.buttonImages:
                startActivity(new Intent(getActivity().getApplicationContext(), ImagesActivity.class));
                break;
            case R.id.buttonMaps:
                startActivity(new Intent(getActivity().getApplicationContext(), MapsActivity.class));
                break;
            case R.id.buttonSettings:
                startActivity(new Intent(getActivity().getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.buttonHelp:
                startActivity(new Intent(getActivity().getApplicationContext(), HelpActivity.class));
                break;
            case R.id.buttonSensors:
                startActivity(new Intent(getActivity().getApplicationContext(), SensorsActivity.class));
                break;
            case R.id.buttonExit: {
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), MediaPlaybackService.class));
                showQuitAppDialog();
            }
            break;
        }
    }

    private void showQuitAppDialog() {
        FragmentManager manager = getFragmentManager();
        QuitAppDialogFragment fragment = new QuitAppDialogFragment();
        fragment.show(manager, "quit_app");
    }

}
