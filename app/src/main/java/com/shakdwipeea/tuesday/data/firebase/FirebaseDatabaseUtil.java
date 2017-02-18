package com.shakdwipeea.tuesday.data.firebase;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by akash on 18/2/17.
 */

public class FirebaseDatabaseUtil {
    private static final String TAG = "FirebaseDatabaseUtil";

    private static FirebaseDatabase firebaseDatabase;

    public synchronized static FirebaseDatabase getDatabase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            Log.d(TAG, "getDatabase: setting persistence enabled");
            firebaseDatabase.setPersistenceEnabled(true);
        }

        Log.d(TAG, "getDatabase: returning database");
        return firebaseDatabase;
    }
}
