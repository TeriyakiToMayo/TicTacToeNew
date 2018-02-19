package com.wiley.fordummies.androidsdk.tictactoe;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Created by adamcchampion on 2017/08/12.
 */

public class VideoFragment extends Fragment implements View.OnClickListener {
    private Button mButtonStart, mButtonStop, mButtonRecord;
    private VideoView mVideoView = null;
    private Uri mVideoFileUri = null;
    private Intent mRecordVideoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);

    public static final int VIDEO_CAPTURED = 1;

    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);

        mVideoView = (VideoView) v.findViewById(R.id.videoView);

        mButtonStart = (Button) v.findViewById(R.id.buttonVideoStart);
        mButtonStart.setOnClickListener(this);
        mButtonStop = (Button) v.findViewById(R.id.buttonVideoStop);
        mButtonStop.setOnClickListener(this);
        mButtonRecord = (Button) v.findViewById(R.id.buttonVideoRecord);
        mButtonRecord.setOnClickListener(this);

        Button btnExit = (Button) v.findViewById(R.id.buttonVideoExit);
        btnExit.setOnClickListener(this);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath() +
                File.separator + "sample_video.mp4";
        File videoFile = new File(path);
        if (videoFile.exists()) {
            mVideoFileUri = Uri.fromFile(videoFile);
        }
        else {
            // Video file doesn't exist, so load sample video from resources.
            String videoResourceName = "android.resource://" + getActivity().getPackageName() +
                    File.separator + R.raw.sample_video;
            mVideoFileUri = Uri.parse(videoResourceName);
        }

        // Guard against no audio recorder app (disable the "record" button).
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(mRecordVideoIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mButtonRecord.setEnabled(false);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(getResources().getString(R.string.video));
            }
        }
        catch (NullPointerException npe) {
            Log.e(TAG, "Could not set subtitle");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonVideoStart:
                // Load and start the movie
                mVideoView.setVideoURI(mVideoFileUri);
                mVideoView.start();
                break;
            case R.id.buttonVideoRecord:
                startActivityForResult(mRecordVideoIntent, VIDEO_CAPTURED);
                break;
            case R.id.buttonVideoStop:
                mVideoView.stopPlayback();
                break;
            case R.id.buttonVideoExit:
                getActivity().finish();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURED) {
            mVideoFileUri = data.getData();
        }
    }
}
