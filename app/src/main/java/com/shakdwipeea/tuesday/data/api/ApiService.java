package com.shakdwipeea.tuesday.data.api;

import com.shakdwipeea.tuesday.data.entities.HttpResponse;
import com.shakdwipeea.tuesday.data.entities.user.User;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by ashak on 25-10-2016.
 */

public interface ApiService {
    @POST("/phone")
    Observable<User> getAccountDetails(@Body User user);

    @POST("/register")
    Observable<HttpResponse.GenResponse> saveDetails(@Body User user);

    @POST("/verify")
    Observable<User> verifyOtp(@Body User user);

    @GET("/profile")
    Observable<User> getContact(@Query("phone") String phoneNumber);
}
