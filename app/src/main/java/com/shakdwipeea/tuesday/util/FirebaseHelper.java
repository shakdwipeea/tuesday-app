package com.shakdwipeea.tuesday.util;

import android.util.Log;

import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

import rx.Observable;

/**
 * Created by ashak on 16-10-2016.
 */

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";

    /**
     * Uploads the picture to a firebase storage reference
     * @param stream InputStream of picture to upload
     * @param storageReference FirebaseHelper reference where to upload
     * @return Observable containing download url
     */
    public static Observable<String> uploadPictureFromStream(InputStream stream,
                                                      StorageReference storageReference) {
        return Observable.create(subscriber -> {
            Log.d(TAG, "uploadPictureFromStream: Upload scheduled");
            storageReference.putStream(stream)
                    .addOnFailureListener(subscriber::onError)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "uploadPictureFromStream: upload complete");
                        if (taskSnapshot.getDownloadUrl() != null) {
                           subscriber.onNext(taskSnapshot.getDownloadUrl().toString());
                        } else {
                           subscriber.onError(new NullPointerException("Unable to get url"));
                        }
                        subscriber.onCompleted();
                    });
        });
    }

}
