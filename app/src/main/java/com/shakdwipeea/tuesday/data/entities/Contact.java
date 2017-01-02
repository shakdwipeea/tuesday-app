package com.shakdwipeea.tuesday.data.entities;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by ashak on 07-11-2016.
 */

public class Contact {
    public int id;
    public String name;
    public List<String> phone;
    public String email;
    public String uriString;
    public Bitmap thumbNail;
    public boolean isTuesday;

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", uriString='" + uriString + '\'' +
                '}';
    }
}
