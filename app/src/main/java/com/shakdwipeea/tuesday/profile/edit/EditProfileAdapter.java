package com.shakdwipeea.tuesday.profile.edit;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.data.providers.ProviderService;
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

class EditProfileAdapter
        extends SectionedRecyclerViewAdapter<EditProfileAdapter.EditProfileHeaderViewHolder,
        EditProfileAdapter.ProfileItemViewHolder, EditProfileAdapter.EditProfileFooterViewHolder> {
    private static final String TAG = "EditProfileAdapter";
    private static final int SECTION_CALL = 0;
    private static final int SECTION_EMAIL = 1;
    private static final int SECTION_SOCIAL = 2;
    private List<Provider> callList;
    private List<Provider> mailList;
    private List<Provider> socialList;
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
    protected EditProfileFooterViewHolder onCreateSectionFooterViewHolder(ViewGroup parent,
                                                                          int viewType) {
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
        holder.setSection(section);
    }


    /**
     * Binds data to the item view for a given position within a section
     *
     * @param holder
     * @param section
     * @param position
     */
    @Override
    protected void onBindItemViewHolder(ProfileItemViewHolder holder, int section, int indexPosition) {
        ProviderDetailEditBinding binding = holder.getBinding();
        Provider provider;

        switch (section) {
            case SECTION_CALL: // Call section
                provider = callList.get(indexPosition);
                break;
            case SECTION_EMAIL: // Mail section
                provider = mailList.get(indexPosition);
                break;
            default:
                provider = socialList.get(indexPosition);
        }

        holder.getBinding().setItemDetail(provider.getProviderDetails());
        holder.getBinding().setVm(new EditProfileItemViewModel(editProfilePresenter, provider));

        if (section == SECTION_CALL || section == SECTION_EMAIL) {
            setSpinnerSelection(binding, provider.providerDetails);
        } else {
            setSpinnerSelection(binding, provider.getName());
        }

        holder.updateProvider(provider);
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

    class ProfileItemViewHolder extends RecyclerView.ViewHolder {

        private ProviderDetailEditBinding binding;
        private Provider provider;

        ProfileItemViewHolder(ProviderDetailEditBinding itemViewBinding) {
            super(itemViewBinding.getRoot());

            this.binding = itemViewBinding;
            bindListeners();
        }

        public ProviderDetailEditBinding getBinding() {
            return binding;
        }

        public void updateProvider(Provider provider) {
            this.provider = provider;
        }

        private void bindListeners() {
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
                    .debounce(500, TimeUnit.MILLISECONDS)
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
         *
         * @param oldProvider      ProviderDetail to update
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

                default:
                    return oldProvider;
            }
        }
    }

    class EditProfileFooterViewHolder extends RecyclerView.ViewHolder {
        private int section;

        private EditProfileSectionFooterBinding footerBinding;

        public EditProfileFooterViewHolder(EditProfileSectionFooterBinding footerBinding) {
            super(footerBinding.getRoot());
            this.footerBinding = footerBinding;

            footerBinding.addCallItem.setOnClickListener(v -> {
                if (section == 0) {
                    addPhoneAccount(EditProfileAdapter.this.callList);
                } else if (section == 1) {
                    addMailAccount(EditProfileAdapter.this.mailList);
                } else {
                    addProviderAccount(EditProfileAdapter.this.socialList);
                }
            });
        }

        public void setSection(int section) {
            this.section = section;
        }

        public EditProfileSectionFooterBinding getFooterBinding() {
            return footerBinding;
        }

        /**
         * Get providers which have not already been added
         *
         * @param addedProviders Providers which have been already added
         * @return List of Provider Names which have not been added
         */
        private List<String> newProviderList(List<Provider> addedProviders) {
            ArrayList<String> providerNames = new ArrayList<>(Arrays.asList(ProviderNames.getAll()));

            for (Provider provider :
                    addedProviders) {
                providerNames.remove(provider.getName());
            }

            providerNames.remove(ProviderNames.Call);
            providerNames.remove(ProviderNames.Email);

            return providerNames;
        }

        public void addProviderAccount(List<Provider> socialList) {
            new MaterialDialog.Builder(context)
                    .title(R.string.select_provider_label)
                    .items(newProviderList(socialList))
                    .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                        if (text != null) {
                            addAccount(text.toString());
                            return true;
                        }

                        return false;
                    })
                    .positiveText(R.string.choose)
                    .show();
        }

        public void addPhoneAccount(List<Provider> callList) {
            String providerName = ProviderNames.Call;
            Log.d(TAG, "addPhoneOrEmailAccount: Providername " + providerName);
            rx.Observable.from(callList)
                    .filter(provider -> provider.name.equals(providerName))
                    .toList()
                    .map(this::getRemainingDetailTypes)
                    .subscribe(
                            providers -> {
                                showDialog(ProviderNames.Call, providers);
                            }
                    );
        }

        public void addMailAccount(List<Provider> mailList) {
            String providerName = ProviderNames.Email;
            Log.d(TAG, "addPhoneOrEmailAccount: Providername " + providerName);
            rx.Observable.from(mailList)
                    .filter(provider -> provider.name.equals(providerName))
                    .toList()
                    .map(this::getRemainingDetailTypes)
                    .subscribe(
                            providers -> {
                                showDialog(ProviderNames.Email, providers);
                            }
                    );
        }

        private void showDialog(String providerName, List<String> remainingDetailTypes) {
            new MaterialDialog.Builder(context)
                    .title("Choose a detail type")
                    .items(remainingDetailTypes)
                    .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                        if (text != null && remainingDetailTypes.size() > 0) {
                            addAccount(providerName, text.toString());
                            return true;
                        }

                        return false;
                    })
                    .positiveText(R.string.choose)
                    .show();
        }

        private rx.Observable<List<Provider>> getProvidersWithName(List<Provider> providers, String providerName) {
            return rx.Observable.from(providers)
                    .filter(provider -> provider.name.equals(providerName))
                    .toList();
        }

        /**
         * Given a list of providers of same type, it returns which detail type can be used.
         * This is to be used only with Call and Email Providers.
         * For example, if we have Call_Primary and Call_Work in firebase,
         * then when we reach here We will have two providers with same names Call
         * having different detailType. So passing these two providers as itemList
         * will return the missing detailType. i don't remember the name.
         *
         * @param itemList Provider list of same name
         * @return Remaining detailTypes
         */
        private List<String> getRemainingDetailTypes(List<Provider> itemList) {
            List<String> detailTypes = ProviderDetails.DetailType.getDetailTypes();

            for (Provider p : itemList) {
                detailTypes.remove(p.providerDetails.detailType);
            }

            return detailTypes;
        }

        private void addAccount(String providerName) {
            Log.d(TAG, "addAccount: Provider Name is " + providerName);

            Provider provider = ProviderService.getInstance()
                    .getProviderHashMap().get(providerName);

            Provider pToAdd = new Provider(provider);

            Log.d(TAG, "addAccount: pToAdd " + pToAdd);
//            editProfilePresenter.saveDetails(pToAdd);
            EditProfileAdapter.this.addProvider(pToAdd);
        }

        /**
         * used for phone and email in which case the detail type is required
         *
         * @param providerName Provider to change
         * @param detailType   Detail type
         */
        private void addAccount(String providerName, String detailType) {
            Provider provider = ProviderService.getInstance()
                    .getProviderHashMap().get(providerName);

            Provider pToAdd = new Provider(provider);
            pToAdd.providerDetails.detailType = detailType;

            EditProfileAdapter.this.addProvider(pToAdd);
        }
    }

}
