package com.shakdwipeea.tuesday.data;

import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.shakdwipeea.tuesday.data.entities.CloudinaryUploadResponse;

import java.util.Map;

import rx.Observable;

/**
 * Created by ashak on 16-10-2016.
 */

public class ProfilePicService {
    private static final String TAG = "ProfilePicService";

    /**
     * Upload the profile pic to cloudinary and get the download url
     *
     * @param file The file to upload can be absolute path or an input stream
     * @return Observable containing publicId and url
     */
    public static Observable<CloudinaryUploadResponse> saveProfilePic(Object file) {
        return Observable.create(subscriber -> {
            try {
                Log.d(TAG, "saveProfilePic: starting upload");
                // upload picture
                Cloudinary cloudinary = new Cloudinary(CloudinaryConfig.CLOUDINARY_URL);
                Map uploadResponse = cloudinary.uploader()
                        .upload(file, ObjectUtils.emptyMap());

                Log.d(TAG, "saveProfilePic: Receiving download");
                // receive response
                CloudinaryUploadResponse response = new CloudinaryUploadResponse();
                response.publicId = uploadResponse.get("public_id");
                response.url = uploadResponse.get("url").toString();

                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
                e.printStackTrace();
            }
        });
    }

    public static String transformUrl(CloudinaryUploadResponse response) {
        Cloudinary cloudinary = new Cloudinary(CloudinaryConfig.CLOUDINARY_URL);
        Log.d(TAG, "transformUrl: Transforming the transformer");
        return cloudinary.url()
                .publicId(response.publicId)
                .transformation(new Transformation().width(0.5))
                .generate();
    }
}
