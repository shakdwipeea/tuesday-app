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
    private DatabaseReference userRef;
    private FirebaseUser user;
    private boolean indexed;

    // TODO: 17-11-2016 dispose all the added event listener
    // rename this to FirebaseRepository and make it a subscription model so that listeners
    // can be attached on subscribe() and removed on unsubscribe()
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

    public Observable<Provider> getProvider() {
        return Observable.create(subscriber -> {
           userRef.child(User.UserNode.PROVIDERS)
                   .addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           Log.d(TAG, "onDataChange: Count" + dataSnapshot.getChildrenCount());

                           for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                               ProviderDetails providerDetails = snapshot
                                       .getValue(ProviderDetails.class);

                               Provider provider = ProviderService.getInstance()
                                       .getProviderHashMap()
                                       .get(snapshot.getKey());
                               provider.setProviderDetails(providerDetails);
                               subscriber.onNext(provider);
                               Log.d(TAG, "onDataChange: provider " + provider);
                           }
                           subscriber.onCompleted();
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {
                           subscriber.onError(databaseError.toException());
                       }
                   });
        });
    }

    public Observable<Provider> getProvider(String name) {
        return Observable.create(subscriber -> {
            userRef.child(User.UserNode.PROVIDERS)
                    .child(name)
                    .addValueEventListener(new ValueEventListener() {
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
            userRef.child(User.UserNode.TUES_CONTACTS)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot contactParentDataSnapshot) {
                            GenericTypeIndicator<List<String>> t = new
                                    GenericTypeIndicator<List<String>>() {};

                            Iterable<DataSnapshot> children = contactParentDataSnapshot
                                    .getChildren();

                            for (DataSnapshot contactUid : children) {
                                subscriber.onNext(contactUid.getValue(String.class));
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
        userRef.child(User.UserNode.TUES_CONTACTS)
                .child(contactUid)
                .setValue(true);
    }

    public void setIndexed(boolean indexed) {
        userRef.child(User.UserNode.IS_INDEXED)
                .setValue(indexed);
    }
}
