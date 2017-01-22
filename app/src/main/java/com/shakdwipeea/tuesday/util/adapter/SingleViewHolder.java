package com.shakdwipeea.tuesday.util.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.renderscript.ScriptGroup;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.ProviderPickerItemBinding;

/**
 * Created by akash on 21/1/17.
 */

class SingleViewHolder extends RecyclerView.ViewHolder {
    private ViewDataBinding binding;

    public SingleViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());

        this.binding = binding;
        this.binding.executePendingBindings();
    }

    public ViewDataBinding getBinding() {
        return binding;
    }
}
