package com.shakdwipeea.tuesday.home;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.User;
import com.shakdwipeea.tuesday.databinding.ActivityHomeBinding;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {
    private static final String TAG = "HomeActivity";

    ActivityHomeBinding binding;
    Context context;

    HomePresenter presenter;

    Subscription subscription;

    ContactAdapter searchAdapter;
    ContactAdapter phoneContactAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribe(context);
        setupSearch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unsubscribe();
        subscription.unsubscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        context = this;

        setupTabs();


        searchAdapter = new ContactAdapter();
        phoneContactAdapter = new ContactAdapter();

        // TODO: 07-11-2016 add permission for android 6+
        LinearLayoutManager searchLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager phoneContactLayoutManager = new LinearLayoutManager(this);

        // divider between lists
        // assuming both layoutManager have the same orientation
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                searchLayoutManager.getOrientation());

        // Search listing
        binding.contactList.setLayoutManager(searchLayoutManager);
        binding.contactList.setAdapter(searchAdapter);
        binding.contactList.addItemDecoration(dividerItemDecoration);

        //Phone Contacts
        binding.phoneContactList.setLayoutManager(phoneContactLayoutManager);
        binding.phoneContactList.setAdapter(phoneContactAdapter);
        binding.phoneContactList.addItemDecoration(dividerItemDecoration);
        binding.phoneContactList.setNestedScrollingEnabled(false);

        presenter = new HomePresenter(this);
    }

    @Override
    public void displayPhoneContacts(List<User> users) {
        phoneContactAdapter.setUsers(users);
    }

    @Override
    public void addPhoneContact(User user) {
        phoneContactAdapter.addUser(user);
    }

    private void setupSearch() {
        subscription = RxTextView.textChanges(binding.search)
                .filter(charSequence -> charSequence.length() > 2)
                .debounce(100, TimeUnit.MILLISECONDS)
                .switchMap(charSequence -> presenter.searchName(charSequence.toString()))
                .subscribe(
                        users -> {
                            Log.d(TAG, "setupSearch: Inflating search listing");
                            searchAdapter.setUsers(users);
                            searchAdapter.notifyDataSetChanged();
                        },
                        Throwable::printStackTrace
                );
    }

    private void setupTabs() {
        int tabIconWhiteColor = ContextCompat.getColor(context, R.color.tw__solid_white);
        int tabIconAccentColor = ContextCompat.getColor(context, R.color.colorAccent);

        // Add white filter to all tab icons
        int numTabs = binding.homeTab.getTabCount();
        for (int i = 0; i < numTabs; i++) {
            TabLayout.Tab tab = binding.homeTab.getTabAt(i);

            if (tab != null) {
                Drawable tabIcon = tab.getIcon();
                if (tabIcon != null) {
                    if (tab.isSelected())
                        tabIcon.setColorFilter(tabIconAccentColor, PorterDuff.Mode.SRC_IN);
                    else
                        tabIcon.setColorFilter(tabIconWhiteColor, PorterDuff.Mode.SRC_IN);
                }
            }
        }

        // Make selected tab as accent color
        binding.homeTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Drawable tabIcon = tab.getIcon();
                if (tabIcon != null) {
                    tabIcon.setColorFilter(tabIconAccentColor, PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Drawable tabIcon = tab.getIcon();
                if (tabIcon != null) {
                    tabIcon.setColorFilter(tabIconWhiteColor, PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void displayError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    /**
     * Load a contact photo thumbnail and return it as a Bitmap,
     * resizing the image to the provided image dimensions as needed.
     * @param photoData photo ID Prior to Honeycomb, the contact's _ID value.
     * For Honeycomb and later, the value of PHOTO_THUMBNAIL_URI.
     * @return A thumbnail Bitmap, sized to the provided width and height.
     * Returns null if the thumbnail is not found.
     */
    private Bitmap loadContactPhotoThumbnail(String photoData) {
        // Creates an asset file descriptor for the thumbnail file.
        AssetFileDescriptor afd = null;
        // try-catch block for file not found
        try {
            // Creates a holder for the URI.
            Uri thumbUri;
            // If Android 3.0 or later
            if (Build.VERSION.SDK_INT
                    >=
                    Build.VERSION_CODES.HONEYCOMB) {
                // Sets the URI from the incoming PHOTO_THUMBNAIL_URI
                thumbUri = Uri.parse(photoData);
            } else {
                // Prior to Android 3.0, constructs a photo Uri using _ID
                /*
                 * Creates a contact URI from the Contacts content URI
                 * incoming photoData (_ID)
                 */
                final Uri contactUri = Uri.withAppendedPath(
                        ContactsContract.Contacts.CONTENT_URI, photoData);
                /*
                 * Creates a photo URI by appending the content URI of
                 * Contacts.Photo.
                 */
                thumbUri =
                        Uri.withAppendedPath(
                                contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            }

        /*
         * Retrieves an AssetFileDescriptor object for the thumbnail
         * URI
         * using ContentResolver.openAssetFileDescriptor
         */
            afd = getContentResolver().
                    openAssetFileDescriptor(thumbUri, "r");
        /*
         * Gets a file descriptor from the asset file descriptor.
         * This object can be used across processes.
         */
            FileDescriptor fileDescriptor = afd.getFileDescriptor();
            // Decode the photo file and return the result as a Bitmap
            // If the file descriptor is valid
            if (fileDescriptor != null) {
                // Decodes the bitmap
                return BitmapFactory.decodeFileDescriptor(
                        fileDescriptor, null, null);
            }
            // If the file isn't found
        } catch (FileNotFoundException e) {
            /*
             * Handle file not found errors
             */
            // In all cases, close the asset file descriptor
        } finally {
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {}
            }
        }
        return null;
    }
}
