package com.shakdwipeea.tuesday.data.firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.data.entities.User;

import rx.Observable;

/**
 * Created by ashak on 17-10-2016.
 */

public class UserService {
    private static final String TAG = "UserService";

    private static UserService userService;

    private DatabaseReference dbRef;
    private DatabaseReference userRef;
    private FirebaseUser user;

    private UserService() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = dbRef
                .child(User.KEY)
                .child(user.getUid());
    }

    public static UserService getInstance() {
        if (userService == null)
            userService = new UserService();

        return userService;
    }

    public void saveUserDetails() {
        DatabaseReference userRef = dbRef.child(User.KEY).child(user.getUid());
        userRef.child(User.UserNode.NAME).setValue(user.getDisplayName());

        if (user.getPhotoUrl() != null)
            userRef.child(User.UserNode.PROFILE_PIC).setValue(user.getPhotoUrl().toString());
    }

    public void setHighResProfilePic(Boolean value) {
        Log.d(TAG, "setHighResProfilePic: " + value);
        dbRef.child(User.KEY)
                .child(user.getUid())
                .child(User.UserNode.HAS_HIGH_RES_PROFILE_PIC).setValue(value);
    }

    public Observable<Boolean> hasHighResProfilePic() {
        return Observable.create(subscriber -> {
            dbRef.child(User.KEY)
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

    public void setTuesId(String tuesId) {
        dbRef.child(User.KEY)
                .child(user.getUid())
                .child(User.UserNode.TUES_ID).setValue(tuesId);
    }

    /**
     * Get tues_id
     * @return Observable containing the tues_id
     */
    public Observable<String> getTuesId() {
        return Observable.create(subscriber -> {
           dbRef.child(User.KEY)
                   .child(user.getUid())
                   .child(User.UserNode.TUES_ID)
                   .addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           subscriber.onNext((String) dataSnapshot.getValue());
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

    public Observable<Void> saveProvider(Provider provider) {
        return Observable.create(subscriber -> {
            userRef.child(User.UserNode.PROVIDERS)
                    .child(provider.getName())
                    .setValue(provider.getProviderDetails())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.isComplete()) {
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(task.getException());
                        }
                    })
                    .addOnFailureListener(subscriber::onError);
        });
    }
}
