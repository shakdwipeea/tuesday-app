package com.shakdwipeea.tuesday.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.shakdwipeea.tuesday.data.entities.Contact;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;

/**
 * Created by ashak on 07-11-2016.
 */

public class ContactsService {
    private static ContactsService contactsService;

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

    private ContentResolver contentResolver;

    private ReplaySubject<Contact> replaySubject;

    // would make more sense to inject this via DI
    private ContactsService(Context context) {
        contentResolver = context.getContentResolver();
        replaySubject = ReplaySubject.create();

        getContactsObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(replaySubject);

    }
    public static ContactsService getInstance(Context context) {
        if (contactsService == null) contactsService = new ContactsService(context);

        return contactsService;
    }

    public Observable<Contact> getContacts() {
        return replaySubject;
    }

    private Observable<Contact> getContactsObservable() {
        // TODO: 08-11-2016 prevent flickering of contacts
        return Observable
                .create(subscriber -> {
                    String[] projection = new String[]{
                            CONTACT_ID, DISPLAY_NAME, HAS_PHONE_NUMBER, STARRED_CONTACT};

                    Cursor cursor = contentResolver.query(QUERY_URI, projection,
                            HAS_PHONE_NUMBER + " > 0", null, DISPLAY_NAME + " ASC");
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            subscriber.onNext(getContact(cursor));
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

        getPhone(cursor, contactId, contact);
        getEmail(contactId, contact);
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
                contact.phone = phoneCursor
                        .getString(phoneCursor.getColumnIndex(PHONE_NUMBER));
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
