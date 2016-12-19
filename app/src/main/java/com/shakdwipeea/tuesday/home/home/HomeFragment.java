package com.shakdwipeea.tuesday.home.home;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.FragmentHomeBinding;
import com.shakdwipeea.tuesday.home.HomeActivity;
import com.shakdwipeea.tuesday.profile.ProfileActivity;

import org.parceler.Parcels;

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
    private static final String KEY_RECYCLER_STATE = "recycler_state";

    FragmentHomeBinding binding;

    Context context;

    HomePresenter presenter;

    Subscription subscription;

    ContactAdapter searchAdapter;
    ContactAdapter phoneContactAdapter;
    ContactAdapter tuesContactAdapter;

    MaterialDialog builder;

    static Bundle recyclerViewState;

    boolean editingTuesId = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.subscribe(context);
        setupSearch();
        setRetainInstance(true);
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
        binding.setHandler(presenter);

        setUpBackBehaviour();

        binding.phoneEdit.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                presenter.getTuesContact(textView.getText().toString());
                return true;
            }

            return false;
        });

        return binding.getRoot();
    }

    private void setUpBackBehaviour() {
        HomeActivity activity = (HomeActivity) getActivity();
        activity.setBackPressedListener(presenter.getBackPressedListener());
    }


    @Override
    public void onPause() {
        super.onPause();

        recyclerViewState = new Bundle();
        Parcelable listState = binding.phoneContactList.getLayoutManager().onSaveInstanceState();
        recyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // restore RecyclerView state
        if (recyclerViewState != null) {
            Parcelable listState = recyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            binding.phoneContactList.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    public void displayPhoneContacts(List<User> users) {
        phoneContactAdapter.setUsers(users);
    }

    public void addTuesContact(User user) {
        tuesContactAdapter.addUser(user);
    }

    @Override
    public void clearTuesContact() {
        tuesContactAdapter.clearUsers();
    }

    @Override
    public void showTuesidInput(boolean enable) {
        if (enable) {
            editingTuesId = true;
            binding.tuesidView.setVisibility(View.GONE);
            binding.phoneEdit.setVisibility(View.VISIBLE);
            binding.fab.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_check_black_24dp));
        } else {
            editingTuesId = false;
            binding.phoneEdit.setVisibility(View.GONE);
            binding.tuesidView.setVisibility(View.VISIBLE);
            binding.fab.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_add_black_24dp));
        }
    }

    @Override
    public void openTuesContact(User user) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_EXTRA_KEY, Parcels.wrap(User.class, user));
        startActivity(intent);
    }

    @Override
    public void showProgress(boolean enable) {
        if (enable) {
            builder = new MaterialDialog.Builder(context)
                    .title("Please wait")
                    .content("Loading....")
                    .progress(true, 0)
                    .show();
        } else {
            builder.dismiss();
        }
    }

    @Override
    public void addPhoneContact(User user) {
        phoneContactAdapter.addUser(user);
    }

    @Override
    public void displayTuesIdProgress(Boolean value) {
        if (value) {
            binding.tuesidView.setAllCaps(false);
            binding.tuesidView.setText(getString(R.string.tuesid_progress));
        } else {
            binding.tuesidView.setAllCaps(true);
            binding.tuesidView.setTypeface(DEFAULT_BOLD);
        }
    }

    @Override
    public void displayTuesIdFailure() {
        // Any boolean is not used because this is not transient
        binding.tuesidView.setText(getString(R.string.tuesid_failure));
    }


    private void setupSearch() {
        subscription = RxTextView.textChanges(binding.search)
                .debounce(100, TimeUnit.MILLISECONDS)
                .doOnNext(charSequence -> presenter.searchFriends(charSequence.toString()))
                .subscribe();
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
                    presenter.getContacts(context);
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

            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
            }, REQUEST_WRITE_CONTACTS);

            return false;
        }

        return true;
    }

}
