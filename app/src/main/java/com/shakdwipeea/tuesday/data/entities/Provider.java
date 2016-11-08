package com.shakdwipeea.tuesday.data.entities;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;

import com.shakdwipeea.tuesday.BR;

/**
 * Created by ashak on 08-11-2016.
 */

public class Provider extends BaseObservable{
    private String name;
    private Drawable icon;
    private Type type;
    private boolean selected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
                ", type=" + type +
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
