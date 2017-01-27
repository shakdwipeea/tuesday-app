package com.shakdwipeea.tuesday.profile.edit;

import android.content.Context;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.databinding.ProviderDetailEditBinding;
import com.shakdwipeea.tuesday.util.adapter.ItemViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by akash on 24/1/17.
 */

public class EditProfileItemViewModel
        implements ItemViewModel<ProviderDetailEditBinding, ProviderDetails> {

    public String getDetail(ProviderDetails providerDetails) {
        switch (providerDetails.getType()) {
            case PHONE_NUMBER_VERIFICATION:
            case PHONE_NUMBER_NO_VERIFICATION:
                return providerDetails.getPhoneNumber();

            case USERNAME_NO_VERIFICATION:
            case API_VERIFICATION:
                return providerDetails.getUsername();

            default: return providerDetails.getUsername();
        }
    }

    public void setSpinnerSelection(ProviderDetailEditBinding binding, ProviderDetails details) {
        Context context = binding.getRoot().getContext();

        String[] stringArray = context.getResources().getStringArray(R.array.detail_types);
        ArrayList<String> detailTypes = new ArrayList<>(Arrays.asList(stringArray));
        int detailPos = detailTypes.indexOf(details.detailType);

        binding.detailTypeSpinner.setSelection(detailPos);
    }

    @Override
    public void bindDetail(ProviderDetailEditBinding binding, ProviderDetails item) {
        setSpinnerSelection(binding, item);
    }
}