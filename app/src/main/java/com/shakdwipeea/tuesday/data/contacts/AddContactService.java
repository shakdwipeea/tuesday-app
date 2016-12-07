package com.shakdwipeea.tuesday.data.contacts;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.shakdwipeea.tuesday.data.entities.User;

import java.util.ArrayList;

/**
 * Created by ashak on 04-12-2016.
 */

public class AddContactService {
    private ContentResolver contentResolver;

    public AddContactService(Context context) {
        contentResolver = context.getContentResolver();
    }

    public void  addContact(User user) throws RemoteException, OperationApplicationException {
             contentResolver.applyBatch(ContactsContract.AUTHORITY, makeContact(user));
    }

    public void deleteContact(User user) throws RemoteException, OperationApplicationException {
        String where = "account_name = ? ";
        String[] params = new String[] {user.uid};

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();


        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(where, params)
                .build());

        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
    }

    private ArrayList<ContentProviderOperation> makeContact(User user) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "tuesday")
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, user.uid)
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
