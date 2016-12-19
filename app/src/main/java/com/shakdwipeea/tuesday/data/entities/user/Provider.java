package com.shakdwipeea.tuesday.data.entities.user;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.shakdwipeea.tuesday.BR;

import org.parceler.Parcel;

/**
 * Created by ashak on 08-11-2016.
 */

@Parcel
public class Provider extends BaseObservable{
    public String name;
    public int icon;
    public boolean selected;
    public ProviderDetails providerDetails;

    public ProviderDetails getProviderDetails() {
        return providerDetails;
    }

    public void setProviderDetails(ProviderDetails providerDetails) {
        this.providerDetails = providerDetails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Bindable
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        notifyPropertyChanged(BR.selected);
    }

    @Override
    public String toString() {
        return "Provider{" +
                "name='" + name + '\'' +
                ", icon=" + icon +
                ", selected=" + selected +
                '}';
    }

    public enum Type {
        PHONE_NUMBER_VERIFICATION,
        PHONE_NUMBER_NO_VERIFICATION,
        API_VERIFICATION,
        USERNAME_NO_VERIFICATION
    }
}
