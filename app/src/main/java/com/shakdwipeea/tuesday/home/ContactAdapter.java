package com.shakdwipeea.tuesday.home;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.api.entities.User;
import com.shakdwipeea.tuesday.databinding.ContactItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ashak on 05-11-2016.
 */

class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<User> users;

    private Context context;

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ContactItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(
                        parent.getContext()), R.layout.contact_item, parent, false);
        context = parent.getContext();

        return new ContactViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        User user = users.get(position);

        ContactItemBinding binding = holder.getBinding();
        binding.setContact(user);

        if (user.pic != null && !user.pic.equals("")) {
            Picasso.with(context)
                    .load(user.pic)
                    .into(binding.profilePic);
        } else {
            binding.placeholderProfilePic
                    .setImageDrawable(TextDrawable
                            .builder()
                            .buildRound(
                                    String.valueOf(user.name.charAt(0)),
                                    ColorGenerator.MATERIAL.getColor(user.name)));

            binding.placeholderProfilePic.setVisibility(View.VISIBLE);
            binding.profilePic.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (users != null ? users.size() : 0);
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private ContactItemBinding binding;

        ContactViewHolder(ContactItemBinding viewDataBinding) {
            super(viewDataBinding.getRoot());

            binding = viewDataBinding;
            binding.executePendingBindings();
        }

        public ContactItemBinding getBinding() {
            return binding;
        }
    }
}