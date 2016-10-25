package com.shakdwipeea.tuesday.api.tuesid;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by ashak on 25-10-2016.
 */

public interface TuesIDService {
    @GET("/tuesid")
    Observable<TuesIDResponse> getTuesID();
}
