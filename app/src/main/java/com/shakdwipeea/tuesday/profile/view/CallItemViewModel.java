package com.shakdwipeea.tuesday.profile.view;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.databinding.CallItemBinding;
import com.shakdwipeea.tuesday.util.adapter.ItemViewModel;
import com.shakdwipeea.tuesday.util.perm.PermViewUtil;
import com.shakdwipeea.tuesday.util.perm.RequestPermissionInterface;

/**
 * Created by akash on 21/1/17.
 */

public class CallItemViewModel implements ItemViewModel<CallItemBinding> {
    PermViewUtil permViewUtil;
    RequestPermissionInterface requestPermissionInterface;
    private ProviderDetails providerDetails;
    private String userId;
    private ProfilePresenter presenter;
    private boolean isSelf;

    /**
     * @param permViewUtil               Permission Utility
     * @param requestPermissionInterface Action to perform after getting permission
     * @param userId                     User id of the person logged in
     * @param presenter                  ProfilePresenter
     * @param isSelf                     are we showing the profile of the same user who is logged in
     */
    public CallItemViewModel(PermViewUtil permViewUtil,
                             RequestPermissionInterface requestPermissionInterface,
                             ProviderDetails providerDetails, String userId,
                             ProfilePresenter presenter, boolean isSelf) {
        this.permViewUtil = permViewUtil;
        this.requestPermissionInterface = requestPermissionInterface;
        this.providerDetails = providerDetails;
        this.userId = userId;
        this.presenter = presenter;
        this.isSelf = isSelf;
    }

    public void callPerson(View view, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        permViewUtil.performActionWithPermissions(
                view.getContext(),
                Manifest.permission.CALL_PHONE,
                new String[]{Manifest.permission.CALL_PHONE},
                requestPermissionInterface,
                () -> { view.getContext().startActivity(intent); }
        );
    }

    public void messagePerson(View view, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        view.getContext().startActivity(intent);
    }

    @Override
    public void bindDetail(CallItemBinding binding) {
        // Any required binding can be done here

        // set up access button
        if (!isSelf && providerDetails.isPersonal
                && providerDetails.accessibleBy.indexOf(userId) == -1) {
            binding.requestAccess.setVisibility(View.VISIBLE);
            binding.phoneNumber.setVisibility(View.GONE);
        } else {
            binding.requestAccess.setVisibility(View.GONE);
            binding.phoneNumber.setVisibility(View.VISIBLE);
        }

        // setup request access
        binding.requestAccess.setOnClickListener(v -> {
            presenter.requestSpAccess(ProviderNames.Call,
                    binding.phoneNumberType.getText().toString());
        });
    }
}
