package com.shakdwipeea.tuesday.data.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shakdwipeea.tuesday.data.entities.User;

import static com.twitter.sdk.android.core.TwitterCore.TAG;

/**
 * Created by ashak on 07-11-2016.
 */

public class FirebaseService {
    private DatabaseReference dbRef;

    public FirebaseService() {
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    public void getProfile(String uid) {
        dbRef.child(User.KEY)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
