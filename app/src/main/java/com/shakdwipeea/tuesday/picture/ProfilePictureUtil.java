package com.shakdwipeea.tuesday.picture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.util.DeviceStorage;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.shakdwipeea.tuesday.data.PermConstants.REQUEST_WRITE_EXTERNAL_STORAGE;

/**
 * Created by akash on 27/12/16.
 */

public class ProfilePictureUtil {
    private static final String TAG = "ProfilePictureUtil";

    public static final int PHOTO_PICKER_REQUEST_CODE = 260;
    public static final int REQUEST_IMAGE_CAPTURE = 582;

    private ProfilePicturePresenter presenter;
    private ProfilePictureView pictureView;


    private String currentPhotoPath;

    public ProfilePictureUtil(ProfilePicturePresenter presenter) {
        this.presenter = presenter;
        this.pictureView = presenter.getPictureView();
    }

    public void openImageMenu() {
        new MaterialDialog.Builder(pictureView.getContext())
                .title("Change Profile Picture")
                .items(R.array.items)
                .itemsCallbackSingleChoice(-1, (dialog, view, which, text) -> {
                    /**
                     * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                     * returning false here won't allow the newly selected radio button
                     * to actually be selected.
                     **/
                    switch (which) {
                        case 0:
                            openCamera();
                            return true;
                        case 1:
                            openPhotoPicker();
                            return true;
                        case 2:
                            presenter.deleteProfilePic();
                            return true;
                    }

                    return true;
                })
                .positiveText("Select")
                .show();
    }

    public void openCamera() {
        if (!hasPermission()) {
            return;
        }

        try {
            // create the file on device
            File imageFile = DeviceStorage.createImageFile(pictureView.getContext());
            currentPhotoPath = imageFile.getAbsolutePath();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // check if anyone is present to handle the camera action
            if (takePictureIntent
                    .resolveActivity(pictureView.getContext().getPackageManager()) != null) {
                // get the uri for file using a fileprovider
                Uri photoURI = FileProvider.getUriForFile(
                        pictureView.getContext(),
                        pictureView.getApplicationContext().getPackageName() + ".provider",
                        imageFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // take the picture
                pictureView.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                displayError("Could not find any camera app. :/");
            }
        } catch (IOException e) {
            displayError(e.getMessage());
        }
    }

    public void openPhotoPicker() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        pictureView.startActivityForResult(photoPickerIntent, PHOTO_PICKER_REQUEST_CODE);
    }

    private boolean hasPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(pictureView.getContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // This calls the correct mechanism of requesting permission in both fragment
            // and activity since this function with same signature is defined there
            pictureView.requestPermissions(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    REQUEST_WRITE_EXTERNAL_STORAGE);

            return false;
        }

        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    final Uri imageUri = data.getData();
                    presenter.updateProfilePic(pictureView.getContext(), imageUri);
                } else {
                    // TODO: 19-10-2016 add error handling
                    new File(currentPhotoPath).delete();
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    DeviceStorage.galleryAddPic(pictureView.getContext(), currentPhotoPath);
                    presenter.updateProfilePic(currentPhotoPath);
                } else {
                    // TODO: 19-10-2016 add error handling
                }
        }
    }

    public void displayError(String reason) {
        Log.d(TAG, "displayError: " + reason);
    }
}
