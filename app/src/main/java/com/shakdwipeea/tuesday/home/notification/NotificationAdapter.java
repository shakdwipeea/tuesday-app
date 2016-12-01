package com.shakdwipeea.tuesday.home.notification;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.NotificationDetail;
import com.shakdwipeea.tuesday.databinding.NotificationRequestItemBinding;
import com.shakdwipeea.tuesday.home.home.ContactItemActionHandler;
import com.shakdwipeea.tuesday.util.Util;

import java.util.ArrayList;

/**
 * Created by ashak on 30-11-2016.
 */

public class NotificationAdapter extends
        RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private ArrayList<NotificationDetail> notificationDetails;

    private Context context;

    public NotificationAdapter() {
        this.notificationDetails = new ArrayList<>();
    }

    public void setNotificationDetails(ArrayList<NotificationDetail> notificationDetails) {
        this.notificationDetails = notificationDetails;
        notifyDataSetChanged();
    }

    public void addNotification(NotificationDetail notificationDetail) {
        this.notificationDetails.add(notificationDetail);
        notifyDataSetChanged();
    }

    public void clearNotification() {
        notificationDetails.clear();
        notifyDataSetChanged();
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        NotificationRequestItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.notification_request_item,
                parent,
                false
        );

        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        NotificationDetail notificationDetail = notificationDetails.get(position);
        holder.binding.setProvider(notificationDetail.provider);
        holder.binding.setContact(notificationDetail.user);
        holder.binding.setActionHandler(new ContactItemActionHandler());
        holder.binding.setItemViewModel(new NotificationItemViewModel());
        Util.displayProfilePic(context, holder.binding.profilePic,
                holder.binding.placeholderProfilePic, notificationDetail.user);
    }

    @Override
    public int getItemCount() {
        return this.notificationDetails.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        public NotificationRequestItemBinding binding;

        public NotificationViewHolder(NotificationRequestItemBinding itemView) {
            super(itemView.getRoot());

            this.binding = itemView;
            this.binding.executePendingBindings();
        }
    }
}
