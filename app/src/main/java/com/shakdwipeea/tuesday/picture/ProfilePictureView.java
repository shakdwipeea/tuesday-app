package com.shakdwipeea.tuesday.picture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

/**
 * Created by akash on 28/12/16.
 */

public interface ProfilePictureView {
    Context getContext();
    Context getApplicationContext();

    void startActivityForResult(Intent intent, int requestCode);
    void requestPermissions(String[] permission, int requestCode);

    void setProgressBar(boolean enable);

    void displayProfilePic(String url);

    void displayError(String reason);

    void displayProfilePicFromPath(String filePath);

    void displayProfilePic(Bitmap bitmap);

    Fragment getFragment();
}
