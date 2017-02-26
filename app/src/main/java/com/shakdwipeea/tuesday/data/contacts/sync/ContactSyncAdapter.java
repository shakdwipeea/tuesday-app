package com.shakdwipeea.tuesday.data.contacts.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.contacts.AddContactService;
import com.shakdwipeea.tuesday.data.contacts.ContactsRepo;
import com.shakdwipeea.tuesday.data.entities.Contact;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.data.firebase.FirebaseService;
import com.shakdwipeea.tuesday.data.firebase.UserService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

        contactsRepo = ContactsRepo.getInstance(context, false);
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

        userService = new UserService();

        Observable<Contact> contactsObservable = contactsRepo.getContactsObservable();

        if (contactsObservable == null) {
            Log.e(TAG, "Contacts permission not available");
            return;
        }

        Iterator<Contact> iterator  = contactsObservable
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

                if (user != null) {

                    try {
                        FirebaseService firebaseService = new FirebaseService(user.uid);
                        User firebaseInfo = firebaseService.getProfile()
                                .toBlocking()
                                .single();

                        user.pic = firebaseInfo.pic;

                        Log.d(TAG, "onPerformSync: User for phone " + phoneNumber +
                                " name " + contact.name + " data is " + user);

                        if (!contactsRepo.isContactPresent(user)) {
                            Log.d(TAG, "onPerformSync: contact not present hence adding");
                            addContactService.addContact(user);
                        }

                        setupTag(user);

                        firebaseService.addSavedBy(firebaseUser.getUid());

                    } catch (Exception e) {
                        FirebaseCrash.report(e);
                        e.printStackTrace();
                    }
                }

            }
        }

        Log.d(TAG, "onPerformSync: ended");
    }

    private void setupTag(User user) {
        Map<String, String> defaultFriendValue = new HashMap<>();
        defaultFriendValue.put(user.uid, "true");

        userService.getTuesContactsWithTags()
                .defaultIfEmpty(defaultFriendValue)
                .first()
                .forEach(friendList -> {
                    Log.d(TAG, "onPerformSync: FriendList is " + friendList.size());

                    String savedTag = friendList.get(user.uid);
                    if (TextUtils.isEmpty(savedTag))
                        savedTag = "true";

                    userService.saveTuesContacts(user.uid, savedTag);
                });
    }
}
