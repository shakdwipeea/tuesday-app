package com.shakdwipeea.tuesday.data.contacts.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.contacts.ContactsRepo;

/**
 * Created by akash on 1/1/17.
 */

public class ContactSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "ContactSyncAdapter";

    private ContactsRepo contactsRepo;

    public ContactSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.d(TAG, "ContactSyncAdapter: initialized");
        contactsRepo = ContactsRepo.getInstance(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync: ");
        contactsRepo.getContactsObservable()
                .flatMap(contact -> {
                    Log.d(TAG, "onPerformSync: for contact " + contact);
                    return ApiFactory.getInstance().getContact(contact.phone);
                })
                .onErrorReturn(throwable -> null)
                .toBlocking()
                .forEach(
                        user -> Log.d(TAG, "onPerformSync: Found user" + user)
                );
        Log.d(TAG, "onPerformSync: ended");
    }
}
