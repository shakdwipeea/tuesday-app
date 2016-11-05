package com.shakdwipeea.tuesday.profile;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.BR;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.ContactInfoCardBinding;

import java.util.List;

/**
 * Created by ashak on 09-10-2016.
 */

public class WasteAdapter extends RecyclerView.Adapter<WasteAdapter.ContactItemViewHolder> {
    private List<String> dataSet;

    private LayoutInflater inflater;

    public WasteAdapter(List<String> dataSet, Context context) {
        this.dataSet = dataSet;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ContactItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ContactInfoCardBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.contact_info_card, parent, false);
        return new ContactItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ContactItemViewHolder holder, int position) {
        holder.binding.setVariable(BR.item, dataSet.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    public static class ContactItemViewHolder extends RecyclerView.ViewHolder {
        public ContactInfoCardBinding binding;

        public ContactItemViewHolder(ContactInfoCardBinding contactInfoCardBinding) {
            super(contactInfoCardBinding.getRoot());
            this.binding = contactInfoCardBinding;
        }
    }
}
