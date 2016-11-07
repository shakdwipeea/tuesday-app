package com.shakdwipeea.tuesday.data.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ashak on 25-10-2016.
 */

public class HttpResponse {
    public class TuesIDResponse {
        @SerializedName("tues_id")
        public String tuesID;

        public TuesIDResponse() {}
    }

    public class GenResponse {
        public String message;

        public GenResponse() {}
    }

    public class SearchResponse {
        public String[] results;

        public SearchResponse() {}
    }
}
