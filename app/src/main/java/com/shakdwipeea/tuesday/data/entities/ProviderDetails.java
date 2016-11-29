package com.shakdwipeea.tuesday.data.entities;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by ashak on 11-11-2016.
 */

@Parcel
public class ProviderDetails extends BaseObservable{
    // user details
    public String phoneNumber;
    public String username;
    public String description;
    public boolean isPersonal;
    public Provider.Type type;

    public List<String> accessibleBy;
    public List<String> requestedBY;

    public static class ProviderDetailNode {
        public static String ACCESSIBLE_BY_KEY = "accessibleBy";
        public static String REQUESTED_BY_KEY = "requested_by";
    }

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public boolean isPersonal() {
        return isPersonal;
    }

    public void setPersonal(boolean aPrivate) {
        isPersonal = aPrivate;
        notifyPropertyChanged(BR.personal);
    }

    public Provider.Type getType() {
        return type;
    }

    public void setType(Provider.Type type) {
        this.type = type;
    }
}
