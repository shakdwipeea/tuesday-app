package com.shakdwipeea.tuesday.api;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONException;

import rx.Observable;

/**
 * Created by ashak on 15-10-2016.
 */

public class AuthService {
    private static final String TAG = "AuthService";

    public static String FACEBOOK_AUTH_PROVIDER = "facebook.com";

    /**
     * Creates an observable which will return the profile pic url
     *
     * @return url Observable containing the profile pic
     */
    public static Observable<String> getFbProfilePic() {
        return Observable.create(subscriber -> {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/picture?width=800&height=800&redirect=false",
                    null,
                    HttpMethod.GET,
                    response -> {
                        try {
                            String url = parseUrl(response);
                            subscriber.onNext(url);
                            subscriber.onCompleted();
                        } catch (JSONException | NullPointerException e) {
                            subscriber.onError(e);
                        }
                    }
            ).executeAsync();
        });
    }

    /**
     * get url from GraphResponse
     * @param response response from fb
     * @return Url of profile pic
     * @throws JSONException on error
     */
    private static String parseUrl(GraphResponse response)
            throws JSONException, NullPointerException {
        Log.e(TAG, "parseUrl: " + response);
        return response
                .getJSONObject()
                .getJSONObject("data")
                .getString("url");
    }
}
