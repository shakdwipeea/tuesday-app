package com.shakdwipeea.tuesday.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ashak on 17-10-2016.
 */

public class Util {

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

    /**
     * Utility to show image in circular image view, if the image is null then draw a text
     * drawable in a placeholder image view
     *
     * @param context Context required by Picasso
     * @param profilePicView CircularImageView to display profile pic present
     * @param placeholderView Placeholder ImageView to display the text drawable from User's name
     * @param user User whose picture and/or name is to be displayed
     */
    public static void displayProfilePic(Context context, CircleImageView profilePicView,
                                         ImageView placeholderView, User user) {
        if (user.pic != null && !user.pic.equals("")) {
            Picasso.with(context)
                    .load(user.pic)
                    .into(profilePicView);
        } else if (user.photo != null) {
            profilePicView.setImageBitmap(user.photo);
        } else if (!TextUtils.isEmpty(user.name)){
            // TODO: 17-11-2016 generalize text drawable thingy
            placeholderView
                    .setImageDrawable(
                            TextDrawable.builder()
                                    .buildRound(
                                            String.valueOf(user.name.toUpperCase().charAt(0)),
                                            ColorGenerator.MATERIAL.getColor(user.name))
                    );

            placeholderView.setVisibility(View.VISIBLE);
            profilePicView.setVisibility(View.GONE);
        }
    }

    public static void displaySnack(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .show();
    }

    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable.Transformer<T, T> applyComputationScheduler() {
        return tObservable -> tObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static MaterialDialog.Builder createProgressDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0);
    }


}
