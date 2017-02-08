package com.shakdwipeea.tuesday.profile.edit;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.databinding.ProviderDetailEditBinding;
import com.shakdwipeea.tuesday.util.adapter.ItemViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by akash on 24/1/17.
 */

public class EditProfileItemViewModel
        implements ItemViewModel<ProviderDetailEditBinding> {
    private static final String TAG = "EditProfileItemViewMode";

    private EditProfileContract.ItemPresenter itemPresenter;
    private Provider provider;

    public EditProfileItemViewModel(EditProfileContract.ItemPresenter itemPresenter,
                                    Provider provider) {
        this.itemPresenter = itemPresenter;
        this.provider = provider;
    }

    public String getDetail() {
        ProviderDetails providerDetails = provider.providerDetails;
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

    /**
     * Updates the apt field in ProviderDetail
     * @param oldProvider ProviderDetail to update
     * @param updatedTypeValue New value of the identification type
     * @return Updated ProviderDetails
     */
    private ProviderDetails updateProviderDetailsByType(ProviderDetails oldProvider,
                                                        String updatedTypeValue) {
        switch (oldProvider.getType()) {
            case PHONE_NUMBER_VERIFICATION:
            case PHONE_NUMBER_NO_VERIFICATION:
                oldProvider.setPhoneNumber(updatedTypeValue);
                return oldProvider;

            case USERNAME_NO_VERIFICATION:
            case API_VERIFICATION:
                oldProvider.setUsername(updatedTypeValue);
                return oldProvider;

            default: return oldProvider;
        }
    }

    /**
     * Spinner selection for phones and mails as their spinner value is retreived from
     * detailType
     *
     * @param binding View Binding
     * @param details Detail selected
     */
    public void setSpinnerSelection(ProviderDetailEditBinding binding, ProviderDetails details) {
        Context context = binding.getRoot().getContext();

        String[] stringArray = context.getResources().getStringArray(R.array.detail_types);
        ArrayList<String> detailTypes = new ArrayList<>(Arrays.asList(stringArray));
        int detailPos = detailTypes.indexOf(details.detailType);

        binding.detailTypeSpinner.setSelection(detailPos);
    }

    /**
     * Spinner selection for social providers
     *
     * @param binding View Binding
     * @param name Name of provider
     */
    public void setSpinnerSelection(ProviderDetailEditBinding binding, String name) {
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                binding.getRoot().getContext(),
                android.R.layout.simple_spinner_item, Collections.singletonList(name));
        binding.detailTypeSpinner.setAdapter(stringArrayAdapter);
        binding.detailTypeSpinner.setSelection(0);
    }

    @Override
    public void bindDetail(ProviderDetailEditBinding binding) {
        Log.d(TAG, "bindDetail: " + binding + " " + provider);

        binding.detailContent.setText(getDetail());

        if (provider.getName().equals(ProviderNames.Call) ||
                provider.getName().equals(ProviderNames.Email)) {
            setSpinnerSelection(binding, provider.providerDetails);
        } else {
            setSpinnerSelection(binding, provider.getName());
        }

        binding.setItemDetail(provider.providerDetails);

        // We are attaching a listener to Spinner so that we save every change
        binding.detailTypeSpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                                               long id) {
                        Log.d(TAG, "onItemSelected: Position " + position);

                        binding.detailTypeSpinner.setSelection(position);

                        ProviderDetails item = provider.getProviderDetails();

                        String[] detailType = view.getContext()
                                .getResources().getStringArray(R.array.detail_types);
                        List<String> detailList = Arrays.asList(detailType);

                        String detailSelected = detailList.get(position);

                        item.setDetailType(detailSelected);
                        provider.setProviderDetails(item);

                        itemPresenter.saveDetails(provider);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Nothing selected then what?
                    }
                });

        // Attaching listener to the text field to save all the details
        RxTextView.textChanges(binding.detailContent)
                .debounce(300, TimeUnit.MILLISECONDS)
                .doOnNext(charSequence -> {
                    Log.d(TAG, "bindDetail: Text changed for provider " + provider +
                            " new text -> " + charSequence);
                    ProviderDetails newProvider = updateProviderDetailsByType(
                            provider.providerDetails, charSequence.toString());
                    binding.detailTypeSpinner.getSelectedItem();
                    provider.setProviderDetails(newProvider);
                    itemPresenter.saveDetails(provider);
                })
                .subscribe();


        //Attaching listener to the private field
        binding.detailPrivateCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            provider.providerDetails.isPersonal = isChecked;
            itemPresenter.saveDetails(provider);
        });

        //Attach listener for  delete
        binding.detailDelete.setOnClickListener(v -> {
            Log.d(TAG, "bindDetail: Going to delete provider " + provider);
            itemPresenter.deleteDetail(provider);
        });
    }
}