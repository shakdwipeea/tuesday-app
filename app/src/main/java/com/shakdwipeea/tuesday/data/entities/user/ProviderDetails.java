package com.shakdwipeea.tuesday.data.entities.user;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.firebase.database.DatabaseReference;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.shakdwipeea.tuesday.data.entities.user.ProviderDetails.ProviderDetailNode.DESCRIPTION_KEY;
import static com.shakdwipeea.tuesday.data.entities.user.ProviderDetails.ProviderDetailNode.IS_PERSONAL_KEY;
import static com.shakdwipeea.tuesday.data.entities.user.ProviderDetails.ProviderDetailNode.PHONE_NUMBER_KEY;
import static com.shakdwipeea.tuesday.data.entities.user.ProviderDetails.ProviderDetailNode.PROVIDER_DETAIL_TYPE_KEY;
import static com.shakdwipeea.tuesday.data.entities.user.ProviderDetails.ProviderDetailNode.PROVIDER_TYPE_KEY;
import static com.shakdwipeea.tuesday.data.entities.user.ProviderDetails.ProviderDetailNode.USERNAME_KEY;

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

    // This is the detail type i.e. Primary, Work etc.
    public String detailType;

    public List<String> accessibleBy;
    public List<String> requestedBy;

    public static class ProviderDetailNode {
        public static String ACCESSIBLE_BY_KEY = "accessible_by";
        public static String REQUESTED_BY_KEY = "requested_by";

        public static String PHONE_NUMBER_KEY = "phoneNumber";
        public static String USERNAME_KEY = "username";
        public static String DESCRIPTION_KEY = "description";
        public static String IS_PERSONAL_KEY = "isPersonal";
        public static String PROVIDER_TYPE_KEY = "type";
        public static String PROVIDER_DETAIL_TYPE_KEY = "detailType";
    }

    public static class DetailType {
        final public static String PRIMARY = "Primary";
        final public static String WORK = "Work";
        final public static String HOME = "Home";

        public static ArrayList<String> getDetailTypes() {
            return new ArrayList<>(Arrays.asList(PRIMARY, WORK, HOME));
        }
    }

    /**
     * Save provider details
     * @param reference Reference to the correct provider name
     */
    public void saveProviderDetails(DatabaseReference reference) {
        reference.child(PHONE_NUMBER_KEY).setValue(phoneNumber);
        reference.child(USERNAME_KEY).setValue(username);
        reference.child(DESCRIPTION_KEY).setValue(description);
        reference.child(IS_PERSONAL_KEY).setValue(isPersonal);
        reference.child(PROVIDER_TYPE_KEY).setValue(type);
        reference.child(PROVIDER_DETAIL_TYPE_KEY).setValue(detailType);
    }

    @Bindable
    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
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

    @Override
    public String toString() {
        return "ProviderDetails{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", username='" + username + '\'' +
                ", description='" + description + '\'' +
                ", isPersonal=" + isPersonal +
                ", type=" + type +
                ", detailType='" + detailType + '\'' +
                ", accessibleBy=" + accessibleBy +
                ", requestedBy=" + requestedBy +
                '}';
    }
}
