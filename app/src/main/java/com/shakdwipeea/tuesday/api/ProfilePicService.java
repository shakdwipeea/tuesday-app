package com.shakdwipeea.tuesday.api;

import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.shakdwipeea.tuesday.api.entities.CloudinaryUploadResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import rx.Observable;

/**
 * Created by ashak on 16-10-2016.
 */

public class ProfilePicService {
    private static final String TAG = "ProfilePicService";

    public static Observable<CloudinaryUploadResponse> saveProfilePic(InputStream profileImageStream) {
        return Observable.create(subscriber -> {
            try {
                Log.d(TAG, "saveProfilePic: starting upload");
                // upload picture
                Cloudinary cloudinary = new Cloudinary(CloudinaryConfig.CLOUDINARY_URL);
                Map uploadResponse = cloudinary.uploader()
                        .upload(profileImageStream, ObjectUtils.emptyMap());

                Log.d(TAG, "saveProfilePic: Receiving download");
                // receive response
                CloudinaryUploadResponse response = new CloudinaryUploadResponse();
                response.publicId = uploadResponse.get("public_id");
                response.url = uploadResponse.get("url").toString();

                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
                e.printStackTrace();
            }
        });
    }

    public static String transformUrl(CloudinaryUploadResponse response) {
        Cloudinary cloudinary = new Cloudinary(CloudinaryConfig.CLOUDINARY_URL);
        return cloudinary.url()
                .publicId(response.publicId)
                .transformation(new Transformation().width(0.5))
                .generate();
    }
}
