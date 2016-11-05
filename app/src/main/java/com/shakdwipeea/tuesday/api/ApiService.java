package com.shakdwipeea.tuesday.api;

import com.shakdwipeea.tuesday.api.entities.HttpResponse;
import com.shakdwipeea.tuesday.api.entities.User;

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
    @GET("/tuesid")
    Observable<HttpResponse.TuesIDResponse> getTuesID();

    @POST("/register")
    Observable<HttpResponse.GenResponse> indexName(@Body User user);

    @GET("/search")
    Observable<List<User>> searchName(@Query("key") String prefix);
}