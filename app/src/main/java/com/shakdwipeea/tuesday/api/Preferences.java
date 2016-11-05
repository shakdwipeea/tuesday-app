package com.shakdwipeea.tuesday.api;

import android.content.SharedPreferences;

/**
 * Created by ashak on 15-10-2016.
 */

public class Preferences {
    public static String SHARED_PREFS_FILE_NAME = "com.shakdwipeea.tuesday.preferences";

    private static String KEY_NAME_INDEXED = "name_indexed";

    public static boolean isNameIndexed(SharedPreferences preferences) {
        return preferences.getBoolean(KEY_NAME_INDEXED, false);
    }

    public static void setNameIndexed(SharedPreferences preferences, boolean value) {
        preferences.edit()
                .putBoolean(KEY_NAME_INDEXED, value)
                .apply();
    }
}
