package com.shakdwipeea.tuesday.profile.view;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.databinding.MailItemBinding;
import com.shakdwipeea.tuesday.util.adapter.ItemViewModel;

/**
 * Created by akash on 22/1/17.
 */

public class MailItemViewModel implements ItemViewModel<MailItemBinding> {
    private ProviderDetails providerDetails;
    private String userId;
    private ProfilePresenter presenter;
    private boolean isSelf;

    /**
     * Constructor
     *
     * @param providerDetails Provider Details being displayed
     * @param userId          User id of the person logged in
     * @param presenter       ProfilePresenter
     * @param isSelf          are we showing the profile of the same user who is logged in
     */
    public MailItemViewModel(ProviderDetails providerDetails, String userId,
                             ProfilePresenter presenter, boolean isSelf) {
        this.providerDetails = providerDetails;
        this.userId = userId;
        this.presenter = presenter;
        this.isSelf = isSelf;
    }

    public void emailPerson(View view, String email) {
        if (providerDetails.accessibleBy.indexOf(userId) != -1) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + email));
            view.getContext().startActivity(intent);
        }
    }

    @Override
    public void bindDetail(MailItemBinding binding) {
        // Any required binding can be done here

        // set up access button
        if (!isSelf && providerDetails.isPersonal
                && providerDetails.accessibleBy.indexOf(userId) == -1) {
            binding.requestAccess.setVisibility(View.VISIBLE);
            binding.email.setVisibility(View.GONE);
        } else {
            binding.requestAccess.setVisibility(View.GONE);
            binding.email.setVisibility(View.VISIBLE);
        }

        // setup request access
        binding.requestAccess.setOnClickListener(v ->
                presenter.requestSpAccess(ProviderNames.Email,
                        binding.emailType.getText().toString()));

    }
}
