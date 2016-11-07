package com.shakdwipeea.tuesday.data;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;

import rx.Observable;

/**
 * Created by ashak on 15-10-2016.
 */

public class AuthService {
    private static final String TAG = "AuthService";

    public static String FACEBOOK_AUTH_PROVIDER = "facebook.com";
    public static String GOOGLE_AUTH_PROVIDER = "google.com";
    public static String TWITTER_AUTH_PROVIDER = "twitter.com";

    /**
     * Creates an observable which will return the profile pic url
     *
     * @return url Observable containing the profile pic
     */
    public static Observable<String> getFbProfilePic() {
        return Observable.create(subscriber -> {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/picture?width=400&height=400&redirect=false",
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

    public static Observable<String> getTwitterProfilePic() {
        return Observable.create(subscriber -> {
            Twitter.getApiClient().getAccountService().verifyCredentials(
                    false,
                    true,
                    new Callback<User>() {
                        @Override
                        public void success(Result<User> result) {
                            subscriber.onNext(result.data.profileImageUrl);
                            subscriber.onCompleted();
                        }

                        @Override
                        public void failure(TwitterException exception) {
                            subscriber.onError(exception);
                        }
                    });
        });
    }
}
