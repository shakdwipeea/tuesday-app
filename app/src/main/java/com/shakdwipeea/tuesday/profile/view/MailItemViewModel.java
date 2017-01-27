package com.shakdwipeea.tuesday.profile.view;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.databinding.CallItemBinding;
import com.shakdwipeea.tuesday.databinding.MailItemBinding;
import com.shakdwipeea.tuesday.profile.NoOpViewModel;
import com.shakdwipeea.tuesday.util.adapter.ItemViewModel;

/**
 * Created by akash on 22/1/17.
 */

public class MailItemViewModel implements ItemViewModel<MailItemBinding,ProviderDetails> {
    public void emailPerson(View view, String email) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + email));
        view.getContext().startActivity(intent);
    }

    @Override
    public void bindDetail(MailItemBinding binding, ProviderDetails item, int position) {
        // Any required binding can be done here
    }
}
