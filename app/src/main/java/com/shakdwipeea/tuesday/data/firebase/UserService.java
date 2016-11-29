package com.shakdwipeea.tuesday.data.firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.data.entities.ProviderDetails;
import com.shakdwipeea.tuesday.data.entities.User;
import com.shakdwipeea.tuesday.data.providers.ProviderService;

import java.util.List;

import rx.Observable;

/**
 * Created by ashak on 17-10-2016.
 */

public class UserService {
    private static final String TAG = "UserService";

    private static UserService userService;

    private DatabaseReference dbRef;
    private DatabaseReference profileRef;
    private DatabaseReference userRef;
    private FirebaseUser user;
    private boolean indexed;

    // TODO: 17-11-2016 dispose all the added event listener
    // rename this to FirebaseRepository and make it a subscription model so that listeners
    // can be attached on subscribe() and removed on unsubscribe()
    private UserService() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        userRef = dbRef
                .child(User.KEY);
        profileRef = userRef
                .child(user.getUid());

    }

    public static UserService getInstance() {
        if (userService == null)
            userService = new UserService();

        return userService;
    }

    public Observable<User> getUserDetails() {
        return Observable.create(subscriber -> {
           profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   User user = dataSnapshot.getValue(User.class);
                   user.uid = dataSnapshot.getKey();

                   subscriber.onNext(user);
                   subscriber.onCompleted();
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {
                    subscriber.onError(databaseError.toException());
               }
           });
        });
    }

    public void saveUserDetails() {
        DatabaseReference userRef = dbRef.child(User.KEY).child(user.getUid());
        if (user.getDisplayName() != null)
            userRef.child(User.UserNode.NAME).setValue(user.getDisplayName());
        else
            Log.e(TAG, "saveUserDetails: " + user );

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
                   .addListenerForSingleValueEvent(new ValueEventListener() {
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
                            if (request.getPhotoUri() != null)
                                updateProfilePicDatabase(request.getPhotoUri().toString());

                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(task.getException());
                        }
                    })
                    .addOnFailureListener(subscriber::onError);
        });
    }

    public void updateProfilePicDatabase(String url) {
        profileRef.child(User.UserNode.PROFILE_PIC)
                .setValue(url);
    }

    public Observable<Void> saveProvider(Provider provider) {
        return Observable.create(subscriber -> {
            profileRef.child(User.UserNode.PROVIDERS)
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

    public Observable<Provider> getProvider() {
        return FirebaseService.getProviderInfo(profileRef);
    }

    public Observable<Provider> getProvider(String name) {
        return Observable.create(subscriber -> {
            profileRef.child(User.UserNode.PROVIDERS)
                    .child(name)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ProviderDetails providerDetails = dataSnapshot
                                    .getValue(ProviderDetails.class);

                            Provider provider = ProviderService.getInstance()
                                    .getProviderHashMap().get(dataSnapshot.getKey());
                            provider.setProviderDetails(providerDetails);

                            subscriber.onNext(provider);
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            subscriber.onError(databaseError.toException());
                        }
                    });
        });
    }

    public Observable<String> getTuesContacts() {
        // TODO: 17-11-2016 investigate what happens here when a new friend is added
        return Observable.create(subscriber -> {
            profileRef.child(User.UserNode.TUES_CONTACTS)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot contactParentDataSnapshot) {
                            GenericTypeIndicator<List<String>> t = new
                                    GenericTypeIndicator<List<String>>() {};

                            Iterable<DataSnapshot> children = contactParentDataSnapshot
                                    .getChildren();

                            for (DataSnapshot contactUid : children) {
                                subscriber.onNext(contactUid.getKey());
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            subscriber.onError(databaseError.toException());
                        }
                    });

        });
    }

    public void saveTuesContacts(String contactUid) {
        profileRef.child(User.UserNode.TUES_CONTACTS)
                .child(contactUid)
                .setValue(true);
    }

    public void removeTuesContact(String contactUid) {
        profileRef.child(User.UserNode.TUES_CONTACTS)
                .child(contactUid)
                .removeValue();
    }

    public void setIndexed(boolean indexed) {
        profileRef.child(User.UserNode.IS_INDEXED)
                .setValue(indexed);
    }

    public Observable<User> getFriends() {
        return getTuesContacts()
                .flatMap(s -> {
                    FirebaseService firebaseService = new FirebaseService(s);
                    return firebaseService.getProfile();
                });
    }
}
