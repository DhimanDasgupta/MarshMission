package com.dhimandasgupta.marshmission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int READ_EXTERNAL_STORAGE_PERMISSION = 100;
    private static final int CAMERA_PERMISSION = 102;

    private static final int PICK_IMAGE_REQUEST_CODE = 1000;
    private static final int TAKE_PICTURE_REQUEST_CODE = 1001;

    private AppCompatImageView mImageView;

    private Uri mCameraImageFile = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (AppCompatImageView) findViewById(R.id.activity_main_image_view);

        final AppCompatButton galleryImageView = (AppCompatButton) findViewById(R.id.activity_main_gallery_button);
        if (galleryImageView != null) {
            galleryImageView.setOnClickListener(this);
        }

        final AppCompatButton cameraImageView = (AppCompatButton) findViewById(R.id.activity_main_camera_button);
        if (cameraImageView != null) {
            cameraImageView.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_gallery_button:
                onGalleryClicked();
                break;

            case R.id.activity_main_camera_button:
                onCameraClicked();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION :
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPickImage();
                } else {
                    Toast.makeText(getApplicationContext(), "Storage Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case CAMERA_PERMISSION :
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestTakeImage();
                } else {
                    Toast.makeText(getApplicationContext(), "Camera Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    resolvePickedImage(data);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not found Image", Toast.LENGTH_SHORT).show();
                }
                break;

            case TAKE_PICTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    resolveTakeImage();
                } else {
                    Toast.makeText(getApplicationContext(), "Could not found Image", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onGalleryClicked() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation, if Permission id denied previously
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(getApplicationContext(), "Need your permission to pick image", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this,
                    // Need Only Read Permission
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSION);
        } else {
            requestPickImage();
        }
    }

    private void onCameraClicked() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation, if Permission id denied previously
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                Toast.makeText(getApplicationContext(), "Need your permission to take image", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this,
                    // Need Both Camera and Write permission
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_PERMISSION);
        } else {
            requestTakeImage();
        }
    }

    private void requestPickImage() {
        final Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE);
    }

    private void resolvePickedImage(Intent data) {
        final Uri imageUri = data.getData();

        if (mImageView != null) {
            mImageView.setImageURI(imageUri);
        }
    }

    private void requestTakeImage() {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mCameraImageFile = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageFile);

        if (intent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);
        } else {
            Toast.makeText(getApplicationContext(), "No Camera Application found....", Toast.LENGTH_SHORT).show();
        }
    }

    private void resolveTakeImage() {
        if (mImageView != null && mCameraImageFile != null) {
            mImageView.setImageURI(mCameraImageFile);
        }
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }
}
