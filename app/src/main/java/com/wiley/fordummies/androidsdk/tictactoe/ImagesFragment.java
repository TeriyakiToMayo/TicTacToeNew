package com.wiley.fordummies.androidsdk.tictactoe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment for showing and capturing images.
 *
 * Created by adamcchampion on 2017/08/12.
 */

public class ImagesFragment extends Fragment implements View.OnClickListener {
    private ImageView imageView = null;
    public final static int IMAGE_CAPTURED = 1;
    private static Uri imageFileURI;
    private String imageFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() +
            File.separator + "other_image.png";
    private Bitmap imageBitmap = null;
    private final String TAG = getClass().getSimpleName();
    private Intent mCaptureImageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_images, container, false);

        imageView = (ImageView) v.findViewById(R.id.imageView);

        Button buttonShow = (Button) v.findViewById(R.id.buttonImageShow);
        buttonShow.setOnClickListener(this);
        Button buttonCapture = (Button) v.findViewById(R.id.buttonImageCapture);
        buttonCapture.setOnClickListener(this);
        Button buttonExit = (Button) v.findViewById(R.id.buttonImageExit);
        buttonExit.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonImageShow:
                File imageFile = new File(imageFilePath);
                if (imageFile.exists()) {
                    imageBitmap = BitmapFactory.decodeFile(imageFilePath);
                    imageView.setImageBitmap(imageBitmap);
                } else {
                    // File doesn't exist, so load a sample SVG image.
                    // Disable hardware acceleration for SVGs
                    imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    imageView.setImageResource(R.drawable.ic_scoreboard);
                }
                break;
            case R.id.buttonImageCapture:
                startActivityForResult(mCaptureImageIntent, IMAGE_CAPTURED);
                break;
            case R.id.buttonImageExit:
                getActivity().finish();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent cameraIntent) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURED) {
            Bundle extras = cameraIntent.getExtras();
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
            }
        }
    }

}
