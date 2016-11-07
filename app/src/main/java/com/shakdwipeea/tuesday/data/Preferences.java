package com.shakdwipeea.tuesday.data;

import android.content.SharedPreferences;

/**
 * Created by ashak on 15-10-2016.
 */

public class Preferences {
    public static String SHARED_PREFS_FILE_NAME = "com.shakdwipeea.tuesday.preferences";

    private static String KEY_NAME_INDEXED = "name_indexed";

    // TODO: 07-11-2016 clear preferences at log out
    public static boolean isNameIndexed(SharedPreferences preferences) {
        return preferences.getBoolean(KEY_NAME_INDEXED, false);
    }

    public static void setNameIndexed(SharedPreferences preferences, boolean value) {
        preferences.edit()
                .putBoolean(KEY_NAME_INDEXED, value)
                .apply();
    }
}
