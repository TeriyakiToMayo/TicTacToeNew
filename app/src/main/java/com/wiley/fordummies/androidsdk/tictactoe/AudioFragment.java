package com.wiley.fordummies.androidsdk.tictactoe;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Audio playback Fragment.
 *
 * Created by adamcchampion on 2017/08/12.
 */

public class AudioFragment extends Fragment implements View.OnClickListener {
    private boolean mStarted = false;
    private String mAudioFilePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath() +
            File.separator + "sample_audio.mp3";
    private static final int AUDIO_CAPTURED = 1;
    private Uri mAudioFileUri;
    private Intent mRecordAudioIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);

    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_audio, container, false);

        Button buttonStart = (Button) v.findViewById(R.id.buttonAudioStart);
        buttonStart.setOnClickListener(this);
        Button buttonStop = (Button) v.findViewById(R.id.buttonAudioStop);
        buttonStop.setOnClickListener(this);
        Button buttonRecord = (Button) v.findViewById(R.id.buttonAudioRecord);
        buttonRecord.setOnClickListener(this);

        Button btnExit = (Button) v.findViewById(R.id.buttonAudioExit);
        btnExit.setOnClickListener(this);

        File audioFile = new File(mAudioFilePath);
        if (audioFile.exists()) {
            mAudioFileUri = Uri.fromFile(new File(mAudioFilePath));
        }
        else {
            // Audio file doesn't exist, so load sample audio from resources.
            String audioResourceName = "android.resource://" + getActivity().getPackageName() +
                    File.separator + R.raw.sample_audio;
            mAudioFileUri = Uri.parse(audioResourceName);
        }

        // Guard against no audio recorder app (disable the "record" button).
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(mRecordAudioIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            buttonRecord.setEnabled(false);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(getResources().getString(R.string.audio));
            }
        }
        catch (NullPointerException npe) {
            Log.e(TAG, "Could not set subtitle");
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAudioStart:
                if (!mStarted) {
                    Intent musicIntent = new Intent(getActivity().getApplicationContext(), MediaPlaybackService.class);
                    musicIntent.putExtra("URIString", mAudioFileUri.toString());
                    Log.d(TAG, "URI: " + mAudioFileUri.toString());
                    getActivity().startService(musicIntent);
                    mStarted = true;
                }
                break;
            case R.id.buttonAudioStop:
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), MediaPlaybackService.class));
                mStarted = false;
                break;
            case R.id.buttonAudioRecord:
                startActivityForResult(mRecordAudioIntent, AUDIO_CAPTURED);
                break;
            case R.id.buttonAudioExit:
                getActivity().finish();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == AUDIO_CAPTURED) {
            mAudioFileUri = data.getData();
            Log.v(TAG, "Audio File URI: " + mAudioFileUri);
        }
    }
}
