package com.shakdwipeea.tuesday.data.contacts;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.contacts.sync.SyncUtils;
import com.shakdwipeea.tuesday.data.entities.user.User;

import java.util.ArrayList;

/**
 * Created by ashak on 04-12-2016.
 */

public class AddContactService {
    private static final String TAG = "AddContactService";

    private ContentResolver contentResolver;
    private Preferences preferences;

    public AddContactService(Context context) {
        contentResolver = context.getContentResolver();
        preferences = Preferences.getInstance(context);
    }

    public void addContact(User user) throws RemoteException, OperationApplicationException {
        if (!preferences.isSync()) {
            Log.d(TAG, "addContact: Sync not enabled");
            return;
        }

        if (user.phoneNumber != null)
             contentResolver.applyBatch(ContactsContract.AUTHORITY, makeContact(user));
        else
            Log.d(TAG, "addContact: why u adding this " + user);
    }

    public void deleteContact(User user) throws RemoteException, OperationApplicationException {
        if (!preferences.isSync()) {
            Log.d(TAG, "deleteContact: Sync not enabled");
            return;
        }

        String whereName = ContactsContract.RawContacts.ACCOUNT_NAME + " = ? ";
        String[] nameParams = new String[] {user.phoneNumber};

        String whereType = ContactsContract.RawContacts.ACCOUNT_TYPE + " = ? ";
        String[] typeParams = new String[] { SyncUtils.ACCOUNT_TYPE };

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();


        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(whereName, nameParams)
                .withSelection(whereType, typeParams)
                .build());

        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
    }

    private ArrayList<ContentProviderOperation> makeContact(User user) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, SyncUtils.ACCOUNT_TYPE)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, user.phoneNumber)
                .build());


        //------------------------------------------------------ Names
        if (user.name != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            user.name).build());
        }

        //------------------------------------------------------ Mobile Number
        if (user.phoneNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, user.phoneNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Home Numbers
//        if (HomeNumber != null) {
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
//                    .build());
//        }

        //------------------------------------------------------ Work Numbers
//        if (WorkNumber != null) {
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
//                    .build());
//        }

        //------------------------------------------------------ Email
        if (user.email != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, user.email)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Organization
//        if (!company.equals("") && !jobTitle.equals("")) {
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
//                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
//                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//                    .build());
//        }
        return ops;
    }
}
