package com.shakdwipeea.tuesday.data.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import rx.Observable;
import rx.subscriptions.Subscriptions;

/**
 * Created by ashak on 29-11-2016.
 */

public class RxFirebase {

    static Observable<DataSnapshot> getData(DatabaseReference databaseReference) {
        return Observable.create(subscriber -> {
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    subscriber.onNext(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    subscriber.onError(databaseError.toException());
                }
            };

            subscriber.add(
                    Subscriptions.create(() -> databaseReference.removeEventListener(eventListener))
            );

            databaseReference.addValueEventListener(eventListener);
        });
    }

    static Observable<String> getChildKeys(DatabaseReference databaseReference) {
        return getData(databaseReference)
                .flatMap(dataSnapshot -> Observable.from(dataSnapshot.getChildren()))
                .map(DataSnapshot::getKey);
    }
}
