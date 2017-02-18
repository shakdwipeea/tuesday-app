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

    private static String KEY_SYNC_FLAG = "sync_contact";

    private static String KEY_LOGGED_IN = "logged_in";

    private static String KEY_ACCOUNT_NAME = "account_name";

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

    public boolean isSync() {
        return sharedPreferences.getBoolean(KEY_SYNC_FLAG, false);
    }

    public void setSync(boolean enable) {
        sharedPreferences.edit()
                .putBoolean(KEY_SYNC_FLAG, enable)
                .apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        sharedPreferences.edit()
                .putBoolean(KEY_LOGGED_IN, loggedIn)
                .apply();
    }

    public String getAccountName() {
        return sharedPreferences.getString(KEY_ACCOUNT_NAME, "Tuesday");
    }

    public void setAccountName(String accountName) {
        sharedPreferences.edit()
                .putString(KEY_ACCOUNT_NAME, accountName)
                .apply();
    }

    public void clear() {
        setNameIndexed(false);
        setSetupComplete(false);
        setLoggedIn(false);
        setAccountName("Tuesday");
    }
}
