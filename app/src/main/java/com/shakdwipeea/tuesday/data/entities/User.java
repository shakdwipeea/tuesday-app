package com.shakdwipeea.tuesday.data.entities;

import android.graphics.Bitmap;

import org.parceler.Parcel;

/**
 * Created by ashak on 17-10-2016.
 */
@Parcel
public class User {
    //Key for firebase storage of users
    public static String KEY = "users";

    public String name;
    public String pic;
    public String uid;
    public String phoneNumber;
    public String email;
    public Bitmap photo;

    public Boolean hasHighResProfilePic;
    public Boolean isIndexed;
    public String tuesId;

    public User() {}

    // Add these as annotations
    public static class UserNode {
        public static String HAS_HIGH_RES_PROFILE_PIC = "hasHighResProfilePic";
        public static String TUES_ID = "tuesId";
        public static String NAME = "name";
        public static String PROFILE_PIC = "pic";
        public static String PROVIDERS = "providers";
        public static String TUES_CONTACTS = "tues_contacts";
        public static String IS_INDEXED = "isIndexed";
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", pic='" + pic + '\'' +
                ", uid='" + uid + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", photo=" + photo +
                ", hasHighResProfilePic=" + hasHighResProfilePic +
                ", tuesId='" + tuesId + '\'' +
                '}';
    }
}
