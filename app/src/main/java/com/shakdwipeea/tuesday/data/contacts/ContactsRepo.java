package com.shakdwipeea.tuesday.data.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.shakdwipeea.tuesday.data.contacts.sync.SyncUtils;
import com.shakdwipeea.tuesday.data.entities.Contact;
import com.shakdwipeea.tuesday.data.entities.user.User;

import java.util.ArrayList;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;

/**
 * Created by ashak on 07-11-2016.
 */

public class ContactsRepo {
    private static final String TAG = "ContactsRepo";

    private static ContactsRepo contactsRepo;

    private Uri QUERY_URI = ContactsContract.Contacts.CONTENT_URI;
    private String CONTACT_ID = ContactsContract.Contacts._ID;
    private String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    private String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
    private String EMAIL_DATA = ContactsContract.CommonDataKinds.Email.DATA;
    private String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private String STARRED_CONTACT = ContactsContract.Contacts.STARRED;
    private String ACCOUNT_TYPE = ContactsContract.RawContacts.ACCOUNT_TYPE;

    private ContentResolver contentResolver;

    private ReplaySubject<Contact> replaySubject;

    private Context context;

    // would make more sense to inject this via DI
    private ContactsRepo(Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();
        replaySubject = ReplaySubject.create();

        getContactsObservable()
                .subscribeOn(Schedulers.newThread())
                .subscribe(replaySubject);
    }

    public static ContactsRepo getInstance(Context context) {
        if (contactsRepo == null) contactsRepo = new ContactsRepo(context);

        return contactsRepo;
    }

    public boolean isContactPresent(User user) {
        String[] projection = new String[] {
                CONTACT_ID, DISPLAY_NAME, PHONE_NUMBER
        };

        String selection = PHONE_NUMBER + " = ? and " + ACCOUNT_TYPE + " = ?";
        String[] selArgs = new String[]{
                user.phoneNumber,
                SyncUtils.ACCOUNT_TYPE
        };

        Cursor cursor = contentResolver.query(PHONE_CONTENT_URI, projection,
                selection, selArgs, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    public Observable<Contact> getContacts() {
        return replaySubject;
    }

    public Observable<Contact> getContactsObservable() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Log.d(TAG, "getContactsObservable: Creating obeserva");

        // TODO: 08-11-2016 prevent flickering of contacts
        return Observable
                .create(subscriber -> {

                    String[] projection = new String[]{
                            CONTACT_ID, DISPLAY_NAME, HAS_PHONE_NUMBER,
                            ContactsContract.RawContacts.ACCOUNT_TYPE};

                    String[] phoneBookProjection = new String[]{
                            CONTACT_ID, DISPLAY_NAME, HAS_PHONE_NUMBER};

                    Cursor cursor;
                    try {
                        cursor = contentResolver.query(QUERY_URI, projection,
                                HAS_PHONE_NUMBER + " > 0", null, DISPLAY_NAME + " ASC");
                    } catch (IllegalArgumentException e) {
                        cursor = contentResolver.query(QUERY_URI, phoneBookProjection,
                                HAS_PHONE_NUMBER + " > 0", null, DISPLAY_NAME + " ASC");
                        FirebaseCrash.report(new Exception(e.getMessage()));
                    }


                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            Contact contact = getContact(cursor);
                            Log.d(TAG, "getContactsObservable: contact " + contact);
                            subscriber.onNext(contact);
                        }

                        cursor.close();
                    }

                    subscriber.onCompleted();
                });
    }

    private Contact getContact(Cursor cursor) {
        String contactId = cursor.getString(cursor.getColumnIndex(CONTACT_ID));
        String name = (cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
        Uri uri = Uri.withAppendedPath(QUERY_URI, String.valueOf(contactId));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        String intentUriString = intent.toUri(0);

        Contact contact = new Contact();
        contact.id = Integer.valueOf(contactId);
        contact.name = name;
        contact.uriString = intentUriString;
        contact.phone = new ArrayList<>();

        String accountType;
        try {
            accountType = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE));
        } catch (Exception e) {
            accountType = "";
        }

        if (accountType.equals(SyncUtils.ACCOUNT_TYPE)) {
            contact.isTuesday = true;
        }

        getPhone(cursor, contactId, contact);
        //getEmail(contactId, contact);
        getPhoto(contactId, contact);
        return contact;
    }

    private void getEmail(String contactId, Contact contact) {
        Cursor emailCursor = contentResolver.query(EMAIL_CONTENT_URI, null,
                EMAIL_CONTACT_ID + " = ?", new String[]{contactId}, null);

        if (emailCursor == null) return;

        while (emailCursor.moveToNext()) {
            String email = emailCursor.getString(emailCursor.getColumnIndex(EMAIL_DATA));
            if (!TextUtils.isEmpty(email)) {
                contact.email = email;
            }
        }
        emailCursor.close();
    }

    private void getPhone(Cursor cursor, String contactId, Contact contact) {
        int hasPhoneNumber = Integer.parseInt(
                cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

        if (hasPhoneNumber > 0) {
            Cursor phoneCursor = contentResolver.query(PHONE_CONTENT_URI, null,
                    PHONE_CONTACT_ID + " = ?", new String[]{contactId}, null);

            if (phoneCursor == null) return;

            while (phoneCursor.moveToNext()) {
                contact.phone.add(
                        phoneCursor.getString(phoneCursor.getColumnIndex(PHONE_NUMBER))
                );
            }
            phoneCursor.close();
        }
    }

    public void getPhoto(String contactId, Contact contact) {
        Uri contactUri = ContentUris
                .withAppendedId(ContactsContract.Contacts.CONTENT_URI, contact.id);
        Uri photoUri = Uri.
                withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

        Cursor cursor = contentResolver.query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) return;

        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    contact.thumbNail = BitmapFactory.decodeByteArray(data, 0, data.length);
                }
            }
        } finally {
            cursor.close();
        }
    }
}
