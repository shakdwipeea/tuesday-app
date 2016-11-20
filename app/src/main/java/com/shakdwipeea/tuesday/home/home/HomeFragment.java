package com.shakdwipeea.tuesday.home.home;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.User;
import com.shakdwipeea.tuesday.databinding.FragmentHomeBinding;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;

import static android.graphics.Typeface.DEFAULT_BOLD;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements HomeContract.View {
    private static final String TAG = "HomeFragment";

    private static final int REQUEST_WRITE_CONTACTS = 120;

    FragmentHomeBinding binding;

    Context context;

    HomePresenter presenter;

    Subscription subscription;

    ContactAdapter searchAdapter;
    ContactAdapter phoneContactAdapter;
    ContactAdapter tuesContactAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.subscribe(context);
        setupSearch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe();
        subscription.unsubscribe();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        context = inflater.getContext();

        searchAdapter = new ContactAdapter();
        phoneContactAdapter = new ContactAdapter();
        tuesContactAdapter = new ContactAdapter();

        LinearLayoutManager searchLayoutManager = new LinearLayoutManager(context);
        LinearLayoutManager phoneContactLayoutManager = new LinearLayoutManager(context);
        LinearLayoutManager tuesContactLayoutManager = new LinearLayoutManager(context);

        // divider between lists
        // assuming both layoutManager have the same orientation
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                searchLayoutManager.getOrientation());

        // Search listing
        binding.contactList.setLayoutManager(searchLayoutManager);
        binding.contactList.setAdapter(searchAdapter);
        binding.contactList.addItemDecoration(dividerItemDecoration);

        // Tuesday contact listing
        binding.tuesContactList.setLayoutManager(tuesContactLayoutManager);
        binding.tuesContactList.setAdapter(tuesContactAdapter);
        binding.tuesContactList.addItemDecoration(dividerItemDecoration);
        binding.tuesContactList.setNestedScrollingEnabled(false);

        //Phone Contacts
        binding.phoneContactList.setLayoutManager(phoneContactLayoutManager);
        binding.phoneContactList.setAdapter(phoneContactAdapter);
        binding.phoneContactList.addItemDecoration(dividerItemDecoration);
        binding.phoneContactList.setNestedScrollingEnabled(false);
        //binding.phoneContactList.setItemAnimator(new SlideIn);

        presenter = new HomePresenter(this);

        return binding.getRoot();
    }

    @Override
    public void displayPhoneContacts(List<User> users) {
        phoneContactAdapter.setUsers(users);
    }

    @Override
    public void displayTuesId(String tuesId) {
        binding.tuesid.setText(tuesId);
    }

    public void addTuesContact(User user) {
        tuesContactAdapter.addUser(user);
    }

    @Override
    public void addPhoneContact(User user) {
        phoneContactAdapter.addUser(user);
    }

    @Override
    public void displayTuesIdProgress(Boolean value) {
        if (value) {
            binding.tuesid.setAllCaps(false);
            binding.tuesid.setText(getString(R.string.tuesid_progress));
        } else {
            binding.tuesid.setAllCaps(true);
            binding.tuesid.setTypeface(DEFAULT_BOLD);
        }
    }

    @Override
    public void displayTuesIdFailure() {
        // Any boolean is not used because this is not transient
        binding.tuesid.setText(getString(R.string.tuesid_failure));
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

    @Override
    public void displayError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.getContacts();
                } else {
                    displayError("Cannot read contacts now. Fuck you");
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean hasPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.READ_CONTACTS
                    },
                    REQUEST_WRITE_CONTACTS);

            return false;
        }

        return true;
    }

}
