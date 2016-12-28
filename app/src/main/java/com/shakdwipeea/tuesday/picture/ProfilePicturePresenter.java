package com.shakdwipeea.tuesday.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.shakdwipeea.tuesday.data.ProfilePicService;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.util.Util;

import java.io.FileNotFoundException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by akash on 28/12/16.
 */

public class ProfilePicturePresenter {
    private static final String TAG = "ProfilePicturePresenter";

    private ProfilePictureView pictureView;
    private UserService userService;

    public ProfilePicturePresenter(ProfilePictureView pictureView) {
        this.pictureView = pictureView;
        this.userService = UserService.getInstance();
    }

    public void updateProfilePic(Context context, Uri imageUri) {
        try {
            // Get the stream to get the bitmap and display the image
            Bitmap bitmap = BitmapFactory.decodeStream(
                    Util.getInputStreamFromFileUri(context, imageUri)
            );
            pictureView.displayProfilePic(bitmap);

            // get the stream again, to upload and set as profile pic
            updateProfilePicFromObject(Util.getInputStreamFromFileUri(context, imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            pictureView.displayError(e.getMessage());
            //setupProfile();
        }
    }

    /**
     * click handler to delete the profile picture
     */
    public void deleteProfilePic() {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(null)
                .build();

        pictureView.setProgressBar(true);
        userService.updateProfile(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                    //setupProfile();
                    pictureView.setProgressBar(false);
                })
                .subscribe(
                        aVoid -> {},
                        throwable -> {
                            pictureView.setProgressBar(false);
                            pictureView.displayError(throwable.getMessage());
                        }
                );
    }

    public void updateProfilePic(String filePath) {
        updateProfilePicFromObject(filePath);
        pictureView.displayProfilePicFromPath(filePath);
    }

    private void updateProfilePicFromObject(Object file) {
        ProfilePicService.saveProfilePic(file)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ProfilePicService::transformUrl)
                .doOnSubscribe(() -> pictureView.setProgressBar(true))
                .subscribe(
                        this::saveProfilePicture,
                        throwable -> {
                            pictureView.setProgressBar(false);
                            Log.e(TAG, "updateProfilePic: ", throwable);
                            pictureView.displayError(throwable.getMessage());
                            //setupProfile();
                        }
                );
    }

    private void saveProfilePicture(String url) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build();

        userService.updateProfile(profileChangeRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                    pictureView.displayProfilePic(url);
                    userService.setHighResProfilePic(true);
                    pictureView.setProgressBar(false);
                })
                .subscribe(
                        aVoid ->  {},
                        throwable -> {
                            pictureView.setProgressBar(false);
                            pictureView.displayError(throwable.getMessage());
                            //setupProfile();
                        }
                );
    }

}
