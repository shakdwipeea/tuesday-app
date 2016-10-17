package com.shakdwipeea.tuesday.api;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shakdwipeea.tuesday.api.entities.User;

import rx.Observable;

/**
 * Created by ashak on 17-10-2016.
 */

public class UserService {
    private static final String TAG = "UserService";

    private DatabaseReference dbRef;
    private FirebaseUser user;

    public UserService() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setHighResProfilePic(Boolean value) {
        Log.d(TAG, "setHighResProfilePic: " + value);
        dbRef.child("users")
                .child(user.getUid())
                .child(User.UserNode.HAS_HIGH_RES_PROFILE_PIC).setValue(value);
    }

    public Observable<Boolean> hasHighResProfilePic() {
        return Observable.create(subscriber -> {
            dbRef.child("users")
                    .child(user.getUid())
                    .child(User.UserNode.HAS_HIGH_RES_PROFILE_PIC)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null)
                                subscriber.onNext(false);
                            else
                                subscriber.onNext((Boolean) dataSnapshot.getValue());

                            subscriber.onCompleted();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            subscriber.onError(databaseError.toException());
                        }
                    });
        });
    }

    public Observable<Void> updateProfile(UserProfileChangeRequest request) {
        return Observable.create(subscriber -> {
            user.updateProfile(request)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(task.getException());
                        }
                    })
                    .addOnFailureListener(subscriber::onError);
        });
    }
}
