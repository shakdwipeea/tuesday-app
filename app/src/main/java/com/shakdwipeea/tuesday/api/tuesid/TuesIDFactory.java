package com.shakdwipeea.tuesday.api.tuesid;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ashak on 25-10-2016.
 */

public class TuesIDFactory {
    public static String BASE_URL = "http://192.168.0.110:9090";

    private static TuesIDService tuesIDService;

    public static TuesIDService getInstance() {
        if (tuesIDService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            tuesIDService = retrofit.create(TuesIDService.class);
        }

        return tuesIDService;
    }
}
