package com.shakdwipeea.tuesday.data.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shakdwipeea.tuesday.data.entities.NotificationDetail;
import com.shakdwipeea.tuesday.data.entities.user.GrantedToDetails;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.data.providers.ProviderService;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by ashak on 07-11-2016.
 */
// FirebaseService retrieves the entire Profulw
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
                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);
                                user.uid = uid;

                                subscriber.onNext(user);
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(new Throwable("Datasnapshot does not exist  " +
                                        dataSnapshot.getKey()));
                                Log.e(TAG, "onDataChange: Data snapshot was null" +
                                        dataSnapshot.getKey());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            subscriber.onError(databaseError.toException());
                        }
                    });
        });
    }

    public Observable<NotificationDetail> inflateNotificationUser(NotificationDetail notificationDetail) {
        return getProfile()
                .map(user -> {
                    notificationDetail.user = user;
                    return notificationDetail;
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

    public Observable<List<Provider>> getProvider() {
        return getProviderInfo(userRef);
    }

//    public Observable<Provider> populateProviderList(List<Provider> providerList) {
//        return Observable.create(subscriber -> {
//           for (Provider p: providerList) {
//               getAccessedBy(p.name)
//
//           }
//        });
//    }

    public void addAccessGranted(GrantedToDetails details) {
        userRef.child(User.UserNode.GRANTED_BY)
                .child(details.grantedByuid).setValue(details.providerName);
    }

    public static Observable<List<Provider>> getProviderInfo(DatabaseReference profileRef) {
        return RxFirebase
                .getData(profileRef.child(User.UserNode.PROVIDERS))
                .map(dataSnapshot -> {
                    Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                    List<Provider> providerList = new ArrayList<>();

                    for (DataSnapshot snap : dataSnapshots) {
                        ProviderDetails providerDetails = snap.getValue(ProviderDetails.class);

                        DataSnapshot requestedByData = snap
                                .child(ProviderDetails.ProviderDetailNode.REQUESTED_BY_KEY);
                        providerDetails.requestedBy = RxFirebase.getKeys(requestedByData);

                        String providerName = snap.getKey();

                        // extract provider name in other cases
                        if (providerName.startsWith(ProviderNames.Call) ||
                                providerName.startsWith(ProviderNames.Email)) {
                            providerName = ProviderNames.getProvider(providerName);
                        }


                        Provider provider = ProviderService.getInstance()
                                .getProviderHashMap()
                                .get(providerName);

                        if (provider != null) {
                            Provider pToAdd = new Provider(provider);
                            pToAdd.setProviderDetails(providerDetails);

                            Log.d(TAG, "getProviderInfo: provider " + provider);
                            Log.d(TAG, "onDataChange: providerDetails " + provider.providerDetails);
                            providerList.add(pToAdd);
                        }
                    }

                    return providerList;
                });
    }

    public Observable<String> getAccessedBy(String providerName) {
        DatabaseReference reference = userRef.child(User.UserNode.PROVIDERS)
                .child(providerName)
                .child(ProviderDetails.ProviderDetailNode.ACCESSIBLE_BY_KEY);

        return RxFirebase.getChildKeys(reference);
    }

    public void addRequestedBy(String providerName, String requestedByUid) {
        userRef.child(User.UserNode.PROVIDERS)
                .child(providerName)
                .child(ProviderDetails.ProviderDetailNode.REQUESTED_BY_KEY)
                .child(requestedByUid).setValue(true);
    }
}
