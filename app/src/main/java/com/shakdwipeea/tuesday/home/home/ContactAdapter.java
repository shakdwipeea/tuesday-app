package com.shakdwipeea.tuesday.home.home;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.ContactItemBinding;
import com.shakdwipeea.tuesday.util.Util;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ashak on 05-11-2016.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<User> users;

    private Context context;

    public ContactAdapter() {
        users = new ArrayList<>();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyItemRangeInserted(0, users.size());
    }

    public void addUser(User user) {
        this.users.add(user);
        notifyItemInserted(users.size());
    }

    public void clearUsers() {
        users.clear();
        notifyDataSetChanged();
    }

    public void filterUser(String pattern) {
        Observable.from(users)
                .filter(user -> user.name.toLowerCase().contains(pattern.toLowerCase()))
                .subscribeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(
                        filteredUsers -> {
                             users = filteredUsers;
                            notifyDataSetChanged();
                        }
                );
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
        ContactItemActionHandler actionHandler = new ContactItemActionHandler();
        binding.setContact(user);
        binding.setActionHandler(actionHandler);

        Util.displayProfilePic(context, binding.profilePic, user);
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
