package com.shakdwipeea.tuesday.data.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import rx.Observable;
import rx.subscriptions.Subscriptions;

/**
 * Created by ashak on 29-11-2016.
 */

public class RxFirebase {
    private static final String TAG = "RxFirebase";

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
                .flatMap(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        Log.e(TAG, "getChildKeys: It fucking exists");
                        return Observable.just(dataSnapshot.getChildren());
                    }
                    else {
                        Log.e(TAG, "getChildKeys: Oh it was empty" + databaseReference);
                        return Observable.empty();
                    }
                })
                .flatMapIterable(dataSnapshots -> dataSnapshots)
                .map(DataSnapshot::getKey);
    }

    static Observable<ArrayList<String>> getChildKeysAsList(DatabaseReference reference) {
        return getData(reference)
                .map(RxFirebase::getKeys);
    }

    static <T> Observable<ArrayList<T>> getDataList(DatabaseReference reference,
                                                     MapDataToType<T> mapDataToType) {
        return getData(reference)
                .flatMap(dataSnapshot -> Observable.just(dataSnapshot.getChildren()))
                .map(dataSnapshots -> {
                    ArrayList<T> resultSet = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshots) {
                        resultSet.add(mapDataToType.mapToType(snapshot));
                    }

                    return resultSet;
                });
    }

    static ArrayList<String> getKeys(DataSnapshot dataSnapshot) {
        ArrayList<String> keys = new ArrayList<>();

        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
            keys.add(snapshot.getKey());
        }

        return keys;
    }

    interface MapDataToType<T> {
        T mapToType(DataSnapshot dataSnapshot);
    }
}
