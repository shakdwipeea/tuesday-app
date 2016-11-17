package com.shakdwipeea.tuesday.data.entities;

import android.graphics.Bitmap;

import com.google.firebase.database.DataSnapshot;

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
    public String tuesId;

    public User() {}

    // Add these as annotations
    public static class UserNode {
        public static String HAS_HIGH_RES_PROFILE_PIC = "hasHighResProfilePic";
        public static String TUES_ID = "tuesId";
        public static String NAME = "name";
        public static String PROFILE_PIC = "pic";
        public static String PROVIDERS = "providers";
    }

    public static User fromFirebase(DataSnapshot dataSnapshot) {
        return new User();
    }
}
