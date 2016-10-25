package com.shakdwipeea.tuesday.api.entities;

/**
 * Created by ashak on 17-10-2016.
 */

public class User {
    //Key for firebase storage of users
    public static String KEY = "users";

    public static class UserNode {
        public static String HAS_HIGH_RES_PROFILE_PIC = "hasHighResProfilePic";
        public static String TUES_ID = "tues_id";
    }

    public Boolean hasHighResProfilePic;
    public String tuesId;
}
