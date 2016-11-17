package com.shakdwipeea.tuesday.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ashak on 15-10-2016.
 */

public class Preferences {
    private static String SHARED_PREFS_FILE_NAME = "com.shakdwipeea.tuesday.preferences";

    private static String KEY_NAME_INDEXED = "name_indexed";
    private static String KEY_SETUP_DONE = "setup_complete";

    private static Preferences preferences;

    private SharedPreferences sharedPreferences;

    private Preferences(Context context) {
        sharedPreferences= context
                .getSharedPreferences(Preferences.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }

    // TODO: 07-11-2016 clear preferences at log out
    public static Preferences getInstance(Context context) {
        if (preferences == null) preferences = new Preferences(context);

        return preferences;
    }

    public boolean isNameIndexed() {
        return sharedPreferences.getBoolean(KEY_NAME_INDEXED, false);
    }

    public void setNameIndexed(boolean value) {
        sharedPreferences.edit()
                .putBoolean(KEY_NAME_INDEXED, value)
                .apply();
    }

    public boolean isSetupComplete() {
        return sharedPreferences.getBoolean(KEY_SETUP_DONE, false);
    }

    public void setSetupComplete(boolean value) {
        sharedPreferences.edit()
                .putBoolean(KEY_SETUP_DONE, value)
                .apply();
    }
}
