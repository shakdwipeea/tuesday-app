package com.shakdwipeea.tuesday.data.entities.user;

import org.parceler.Parcel;

/**
 * Created by ashak on 08-11-2016.
 */

@Parcel
public class Provider {
    public String name;
    public String subName;
    public int icon;
    public boolean selected;
    public ProviderDetails providerDetails;

    public Provider() {
    }

    public Provider(Provider provider) {
        this(provider.name, provider.subName, provider.icon, provider.selected,
                provider.getProviderDetails().type);
    }

    public Provider(String name, String subName, int icon, boolean selected, Type type) {
        this.name = name;
        this.subName = subName;
        this.icon = icon;
        this.selected = selected;
        this.providerDetails = new ProviderDetails();
        this.providerDetails.setType(type);
    }

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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "Provider{" +
                "name='" + name + '\'' +
                ", subName='" + subName + '\'' +
                ", icon=" + icon +
                ", selected=" + selected +
                ", providerDetails=" + providerDetails +
                '}';
    }

    public enum Type {
        PHONE_NUMBER_VERIFICATION,
        PHONE_NUMBER_NO_VERIFICATION,
        API_VERIFICATION,
        USERNAME_NO_VERIFICATION
    }
}
