package com.shakdwipeea.tuesday.data.entities.user;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by ashak on 17-10-2016.
 */
@Parcel
public class User {
    //Key for firebase storage of users
    public static String KEY = "users";

    public String name;

    @SerializedName("photo")
    public String pic;

    public String uid;

    @SerializedName("phone")
    public String phoneNumber;

    public String email;

    @SerializedName("bitmap")
    public Bitmap photo;

    public Boolean hasHighResProfilePic;
    public Boolean isIndexed;
    public String tuesId;

    public Boolean verified;
    public String otp;
    public String token;

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
        public static String ADDED_BY = "addedBy";
        public static String GRANTED_BY = "grantedBy";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public Boolean getHasHighResProfilePic() {
        return hasHighResProfilePic;
    }

    public void setHasHighResProfilePic(Boolean hasHighResProfilePic) {
        this.hasHighResProfilePic = hasHighResProfilePic;
    }

    public Boolean getIndexed() {
        return isIndexed;
    }

    public void setIndexed(Boolean indexed) {
        isIndexed = indexed;
    }

    public String getTuesId() {
        return tuesId;
    }

    public void setTuesId(String tuesId) {
        this.tuesId = tuesId;
    }
}
