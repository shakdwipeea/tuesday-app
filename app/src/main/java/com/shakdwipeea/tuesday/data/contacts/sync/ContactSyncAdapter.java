package com.shakdwipeea.tuesday.data.contacts.sync;

import android.Manifest;
import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.contacts.AddContactService;
import com.shakdwipeea.tuesday.data.contacts.ContactsRepo;
import com.shakdwipeea.tuesday.data.entities.Contact;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.data.firebase.FirebaseService;
import com.shakdwipeea.tuesday.data.firebase.UserService;

import java.util.Iterator;
import java.util.List;

import rx.Observable;

/**
 * Created by akash on 1/1/17.
 */

public class ContactSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "ContactSyncAdapter";

    private ContactsRepo contactsRepo;
    private AddContactService addContactService;
    private Preferences preferences;

    private UserService userService;

    public ContactSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.d(TAG, "ContactSyncAdapter: initialized");

        contactsRepo = ContactsRepo.getInstance(context);
        addContactService = new AddContactService(context);
        preferences = Preferences.getInstance(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync: ");

        if (!preferences.isSync()) {
            Log.d(TAG, "onPerformSync: Sync not enabled. Exiting...");
            return;
        }


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Log.d(TAG, "onPerformSync: Not logged in");
            return;
        }

        userService = UserService.getInstance();

        Iterator<Contact> iterator = contactsRepo.getContactsObservable()
                .toBlocking()
                .getIterator();

        while (iterator.hasNext()) {
            Contact contact = iterator.next();
            for (String phoneNumber: contact.phone) {
                phoneNumber = phoneNumber.replace("+91", "");
                phoneNumber = phoneNumber.replaceAll("\\s", "");
                User user = ApiFactory.getInstance()
                        .getContact(phoneNumber)
                        .onErrorReturn(throwable -> null)
                        .toBlocking()
                        .single();

                Log.d(TAG, "onPerformSync: User for phone " + phoneNumber +
                        " name " + contact.name + " data is " + user);

                if (user != null) {
                    Log.d(TAG, "onPerformSync: User was not null");

                    if (!contactsRepo.isContactPresent(user)) {
                        Log.d(TAG, "onPerformSync: contact not present hence adding");
                        try {
                            addContactService.addContact(user);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }

                    userService.saveTuesContacts(user.uid);

                    FirebaseService firebaseService = new FirebaseService(user.getUid());
                    firebaseService.addSavedBy(firebaseUser.getUid());
                }

            }
        }

        Log.d(TAG, "onPerformSync: ended");
    }
}
