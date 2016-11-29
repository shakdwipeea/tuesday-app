package com.shakdwipeea.tuesday.data.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.data.entities.ProviderDetails;
import com.shakdwipeea.tuesday.data.entities.User;
import com.shakdwipeea.tuesday.data.providers.ProviderService;

import java.util.List;

import rx.Observable;

/**
 * Created by ashak on 07-11-2016.
 */
// FirebaseService retreives the entire Profulw
public class FirebaseService {
    private static final String TAG = "FirebaseService";

    private DatabaseReference dbRef;
    private DatabaseReference userRef;

    private String uid;

    private List<ValueEventListener> valueEventListeners;

    public FirebaseService(String uid) {
        dbRef = FirebaseDatabase.getInstance().getReference();
        this.uid = uid;
        userRef = dbRef
                .child(User.KEY)
                .child(uid);
    }

    public Observable<User> getProfile() {
        return Observable.create(subscriber -> {
            dbRef.child(User.KEY)
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            user.uid = uid;

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

    public Observable<String> getFriendUid() {
        return Observable.create(subscriber -> {
            dbRef.child(User.KEY)
                    .child(uid)
                    .child(User.UserNode.TUES_CONTACTS)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                            for (DataSnapshot friendSnapshot : children) {
                                subscriber.onNext(friendSnapshot.getKey());
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

    public void addSavedBy(String friendUid) {
        userRef.child(User.UserNode.ADDED_BY)
                .child(friendUid)
                .setValue(true);
    }

    public void removeSavedBy(String friendUid) {
        userRef.child(User.UserNode.ADDED_BY)
                .child(friendUid).removeValue();
    }

    public Observable<String> getSavedBy() {
        return Observable.create(subscriber -> {
            userRef.child(User.UserNode.ADDED_BY)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                            for (DataSnapshot child: children) {
                                subscriber.onNext(child.getKey());
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

    public Observable<Provider> getProvider() {
        return getProviderInfo(userRef);
    }

    public static Observable<Provider> getProviderInfo(DatabaseReference profileRef) {
        return Observable.create(subscriber -> {
            profileRef.child(User.UserNode.PROVIDERS)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
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
}
