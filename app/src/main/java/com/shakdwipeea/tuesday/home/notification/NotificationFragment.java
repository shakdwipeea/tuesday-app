package com.shakdwipeea.tuesday.home.notification;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.NotificationDetail;
import com.shakdwipeea.tuesday.databinding.FragmentNotificationBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements NotificationContract.View {

    FragmentNotificationBinding binding;
    NotificationAdapter adapter;

    NotificationPresenter presenter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.subscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.unSubscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_notification, container, false);

        presenter = new NotificationPresenter(this);

        setupTabs();

        adapter = new NotificationAdapter();
        binding.notificationList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationList.setAdapter(adapter);

        return binding.getRoot();
    }

    private void setupTabs() {
        binding.notificationTabLayout.getTabAt(0).select();
        binding.notificationTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        displayError("Position 0 selected");
                        break;
                    case 1:
                        displayError("Position 1 selected");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void addNotification(NotificationDetail notificationDetail) {
        adapter.addNotification(notificationDetail);
    }

    @Override
    public void clearNotification() {
        adapter.clearNotification();
    }

    @Override
    public void displayProgressBar(boolean enable) {

    }

}
