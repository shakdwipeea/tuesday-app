package com.shakdwipeea.tuesday.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

import rx.Observable;

/**
 * Created by ashak on 17-10-2016.
 */

public class Util {
//    public static Observable<Bitmap> resizeBitmapTo(String photoPath, int height, int width) {
//        return Observable.create(subscriber -> {
//            /* Get the size of the image */
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            bmOptions.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(photoPath, bmOptions);
//            int photoW = bmOptions.outWidth;
//            int photoH = bmOptions.outHeight;
//
//		    /* Figure out which way needs to be reduced less */
//            int scaleFactor = 1;
//            if ((width > 0) || (height > 0)) {
//                scaleFactor = Math.min(photoW/width, photoH/height);
//            }
//
//		    /* Set bitmap options to scale the image decode target */
//            bmOptions.inJustDecodeBounds = false;
//            bmOptions.inSampleSize = scaleFactor;
//
//		    /* Decode the JPEG file into a Bitmap */
//            subscriber.onNext(BitmapFactory.decodeFile(photoPath, bmOptions));
//            subscriber.onCompleted();
//        });
//    }



    public static Observable<Bitmap> resizeBitmapTo(String filePath, int height, int width) {
        return Observable.create(subscriber -> {
            Bitmap image = BitmapFactory.decodeFile(filePath);
            if (image == null) {
                subscriber.onError(new NullPointerException("could not find image"));
            }

		    /* Decode the JPEG file into a Bitmap */
            subscriber.onNext(Bitmap.createScaledBitmap(image, width, height, false));

            subscriber.onCompleted();
        });
    }

    public static Observable<Bitmap> resizeBitmapTo(Bitmap image, int height, int width) {
        return Observable.create(subscriber -> {
		    /* Decode the JPEG file into a Bitmap */
            subscriber.onNext(Bitmap.createScaledBitmap(image, width, height, false));
            subscriber.onCompleted();
        });
    }

    public static InputStream getInputStreamFromFileUri(Context context, Uri uri)
            throws FileNotFoundException {
        return context.getContentResolver().openInputStream(uri);
    }

}
