package com.shakdwipeea.tuesday.data.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shakdwipeea.tuesday.data.entities.User;

import rx.Observable;

/**
 * Created by ashak on 07-11-2016.
 */
// FirebaseService retreives the entire Profulw
public class FirebaseService {
    private static final String TAG = "FirebaseService";

    private DatabaseReference dbRef;

    public FirebaseService() {
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    public Observable<User> getProfile(String uid) {
        return Observable.create(subscriber -> {
            dbRef.child(User.KEY)
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            subscriber.onNext(dataSnapshot.getValue(User.class));
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
