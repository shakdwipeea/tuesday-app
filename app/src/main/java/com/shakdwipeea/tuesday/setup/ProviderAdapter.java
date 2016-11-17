package com.shakdwipeea.tuesday.setup;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.databinding.ProviderPickerItemBinding;
import com.shakdwipeea.tuesday.setup.details.ProviderDetailItemViewModel;

import java.util.List;

/**
 * Created by ashak on 08-11-2016.
 */

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {
    private List<Provider> providers;
    private FragmentChangeListener changeListener;

    private Context context;

    public ProviderAdapter() {
    }

    public ProviderAdapter(List<Provider> providers) {
        this.providers = providers;
    }

    public void setChangeListener(FragmentChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
        notifyDataSetChanged();
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public Provider getProvider(int index) {
        return providers.get(index);
    }

    @Override
    public ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        ProviderPickerItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.provider_picker_item,
                parent,
                false
        );

        return new ProviderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ProviderViewHolder holder, int position) {
        Provider provider = providers.get(position);
        ProviderItemViewModel providerItemViewModel = new ProviderDetailItemViewModel(context);
        providerItemViewModel.setUpSelection(provider);
        providerItemViewModel.setFragmentChangeListener(changeListener);

        holder.binding.setProvider(provider);
        holder.binding.setVm(providerItemViewModel);
    }

    @Override
    public int getItemCount() {
        return providers == null ? 0 : providers.size();
    }

    class ProviderViewHolder extends RecyclerView.ViewHolder {
        ProviderPickerItemBinding binding;

        ProviderViewHolder(ProviderPickerItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            this.binding.executePendingBindings();
        }
    }
}
