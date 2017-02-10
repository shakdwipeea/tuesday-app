package com.shakdwipeea.tuesday.profile.edit;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.databinding.EditProfileSectionFooterBinding;
import com.shakdwipeea.tuesday.databinding.EditProfileSectionHeaderBinding;
import com.shakdwipeea.tuesday.databinding.ProviderDetailEditBinding;
import com.truizlop.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by akash on 9/2/17.
 */

// TODO: 9/2/17 java.lang.IndexOutOfBoundsException: Inconsistency detected.
// Invalid view holder adapter positionViewHolder{359cc82d position=15 id=-1, oldPos=4, pLpos:4
// scrap [attachedScrap] tmpDetached no parent}

class EditProfileAdapter
        extends SectionedRecyclerViewAdapter<EditProfileAdapter.EditProfileHeaderViewHolder,
        EditProfileAdapter.ProfileItemViewHolder, EditProfileAdapter.EditProfileFooterViewHolder> {
    private static final String TAG = "EditProfileAdapter";

    private List<Provider> callList;
    private List<Provider> mailList;
    private List<Provider> socialList;

    private static final int SECTION_CALL = 0;
    private static final int SECTION_EMAIL = 1;
    private static final int SECTION_SOCIAL = 2;

    private Context context;
    private EditProfilePresenter editProfilePresenter;

    public EditProfileAdapter(Context context,
                              EditProfilePresenter editProfilePresenter) {
        this.callList = new ArrayList<>();
        this.mailList = new ArrayList<>();
        this.socialList = new ArrayList<>();

        this.context = context;
        this.editProfilePresenter = editProfilePresenter;
    }

    public List<Provider> getCallList() {
        return callList;
    }

    public List<Provider> getMailList() {
        return mailList;
    }

    public List<Provider> getSocialList() {
        return socialList;
    }

    public void clear() {
        callList.clear();
        mailList.clear();
        socialList.clear();
    }

    public void addProvider(Provider provider) {
        if (provider.name.equals(ProviderNames.Call))
            callList.add(provider);
        else if (provider.name.equals(ProviderNames.Email))
            mailList.add(provider);
        else
            socialList.add(provider);

        notifyDataSetChanged();
    }

    @Override
    public int getSectionCount() {
        return 3;
    }

    /**
     * Returns the number of items for a given section
     *
     * @param section
     */
    @Override
    protected int getItemCountForSection(int section) {
        int count;
        switch (section) {
            case 0:
                count = callList.size();
                break;
            case 1:
                count = mailList.size();
                break;
            default:
                count = socialList.size();
        }

        return count;
    }

    /**
     * Returns true if a given section should have a footer
     *
     * @param section
     */
    @Override
    protected boolean hasFooterInSection(int section) {
        return true;
    }

    /**
     * Creates a ViewHolder of class H for a Header
     *
     * @param parent
     * @param viewType
     */
    @Override
    protected EditProfileHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent,
                                                                          int viewType) {
        EditProfileSectionHeaderBinding binding =  DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.edit_profile_section_header, parent, false);

        return new EditProfileHeaderViewHolder(binding);
    }

    /**
     * Creates a ViewHolder of class F for a Footer
     *
     * @param parent
     * @param viewType
     */
    @Override
    protected EditProfileFooterViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        EditProfileSectionFooterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.edit_profile_section_footer, parent, false);

        return new EditProfileFooterViewHolder(binding);
    }

    /**
     * Creates a ViewHolder of class VH for an Item
     *
     * @param parent
     * @param viewType
     */
    @Override
    protected ProfileItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        ProviderDetailEditBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.provider_detail_edit,
                        parent, false);
        return new ProfileItemViewHolder(binding);
    }

    /**
     * Binds data to the header view of a given section
     *
     * @param holder
     * @param section
     */
    @Override
    protected void onBindSectionHeaderViewHolder(EditProfileHeaderViewHolder holder, int section) {
        EditProfileSectionHeaderBinding binding = holder.getBinding();
        switch (section) {
            case SECTION_CALL:
                binding.setText(ProviderNames.Call);
                break;
            case SECTION_EMAIL:
                binding.setText(ProviderNames.Email);
                break;
            default:
                binding.setText("Social Accounts");
        }
    }

    /**
     * Binds data to the footer view of a given section
     *
     * @param holder
     * @param section
     */
    @Override
    protected void onBindSectionFooterViewHolder(EditProfileFooterViewHolder holder, int section) {
        // TODO: 9/2/17 add listeners here
    }


    /**
     * Binds data to the item view for a given position within a section
     *
     * @param holder
     * @param section
     * @param position
     */
    @Override
    protected void onBindItemViewHolder(ProfileItemViewHolder holder, int section, int position) {
        ProviderDetailEditBinding binding = holder.getBinding();
        Provider provider;

        switch (section) {
            case SECTION_CALL: // Call section
                provider = callList.get(position);
                break;
            case SECTION_EMAIL: // Mail section
                provider = mailList.get(position);
                break;
            default:
                provider = socialList.get(position);
        }

        holder.getBinding().setItemDetail(provider.getProviderDetails());
        holder.getBinding().setVm(new EditProfileItemViewModel(editProfilePresenter, provider));

        if (section == SECTION_CALL || section == SECTION_EMAIL) {
            setSpinnerSelection(binding, provider.providerDetails);
        } else {
            setSpinnerSelection(binding, provider.getName());
        }

        bindListeners(binding, provider);
    }


    private void bindListeners(ProviderDetailEditBinding binding, Provider provider) {
        Log.e(TAG, "bindListeners: " + binding + " " + provider);

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

                        editProfilePresenter.saveDetails(provider);
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
                    Log.e(TAG, "bindListeners: Text changed for provider " + provider +
                            " new text -> " + charSequence);
                    ProviderDetails newProvider = updateProviderDetailsByType(
                            provider.providerDetails, charSequence.toString());
                    binding.detailTypeSpinner.getSelectedItem();
                    provider.setProviderDetails(newProvider);
                    editProfilePresenter.saveDetails(provider);
                })
                .subscribe();


        //Attaching listener to the private field
        binding.detailPrivateCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            provider.providerDetails.isPersonal = isChecked;
            editProfilePresenter.saveDetails(provider);
        });

        //Attach listener for  delete
        binding.detailDelete.setOnClickListener(v -> {
            Log.d(TAG, "bindListeners: Going to delete provider " + provider);
            editProfilePresenter.deleteDetail(provider);
        });
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

        showProviderTypeTextView(binding, false);
    }

    /**
     * Spinner selection for social providers
     *
     * @param binding View Binding
     * @param name Name of provider
     */
    public void setSpinnerSelection(ProviderDetailEditBinding binding, String name) {
        binding.socialDetailType.setText(name);
        showProviderTypeTextView(binding, true);
    }

    private void showProviderTypeTextView(ProviderDetailEditBinding binding, Boolean show) {
        if (show) {
            binding.socialDetailType.setVisibility(View.VISIBLE);
            binding.detailTypeSpinner.setVisibility(View.GONE);
        } else {
            binding.socialDetailType.setVisibility(View.GONE);
            binding.detailTypeSpinner.setVisibility(View.VISIBLE);
        }
    }

    static class ProfileItemViewHolder extends RecyclerView.ViewHolder {

        private ProviderDetailEditBinding binding;

        ProfileItemViewHolder(ProviderDetailEditBinding itemViewBinding) {
            super(itemViewBinding.getRoot());

            this.binding = itemViewBinding;
        }

        public ProviderDetailEditBinding getBinding() {
            return binding;
        }
    }

    static class EditProfileHeaderViewHolder extends RecyclerView.ViewHolder {
        private EditProfileSectionHeaderBinding binding;

        public EditProfileHeaderViewHolder(EditProfileSectionHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public EditProfileSectionHeaderBinding getBinding() {
            return binding;
        }
    }

    static class EditProfileFooterViewHolder extends RecyclerView.ViewHolder {
        private EditProfileSectionFooterBinding footerBinding;

        public EditProfileFooterViewHolder(EditProfileSectionFooterBinding footerBinding) {
            super(footerBinding.getRoot());
            this.footerBinding = footerBinding;
        }

        public EditProfileSectionFooterBinding getFooterBinding() {
            return footerBinding;
        }
    }
}
