package com.shakdwipeea.tuesday.api.entities;

/**
 * Created by ashak on 17-10-2016.
 */

public class User {
    //Key for firebase storage of users
    public static String KEY = "users";

    public String name;
    public String pic;
    public String uid;

    public Boolean hasHighResProfilePic;
    public String tuesId;

    public User() {}

    // Add these as annotations
    public static class UserNode {
        public static String HAS_HIGH_RES_PROFILE_PIC = "hasHighResProfilePic";
        public static String TUES_ID = "tues_id";
        public static String NAME = "name";
        public static String PROFILE_PIC = "profile_pic";
    }
}
