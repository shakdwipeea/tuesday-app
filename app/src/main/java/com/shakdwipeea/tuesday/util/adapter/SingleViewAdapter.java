package com.shakdwipeea.tuesday.util.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akash on 21/1/17.
 */

/**
 * Adapter which can be used to quickly show list of a particular type
 *
 * @param <ItemType> ItemType whose list is to be displayed
 * @param <ItemViewModelType> Viewholder responsible to bind the views for data
 */
public class SingleViewAdapter<ItemType, ItemViewModelType>
        extends RecyclerView.Adapter<SingleViewHolder> {

    private ItemViewModelType itemViewModel;
    private List<ItemType> itemList = new ArrayList<>();

    private int layoutId;

    public SingleViewAdapter(ItemViewModelType itemViewModel, int layoutId) {
        this.itemViewModel = itemViewModel;
        this.layoutId = layoutId;
    }

    public void setItemList(List<ItemType> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public void addItem(ItemType item) {
        this.itemList.add(item);
        this.notifyItemInserted(this.itemList.size());
    }

    public void clear() {
        this.itemList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public SingleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), layoutId, null, false
        );

        return new SingleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(SingleViewHolder holder, int position) {
        holder.getBinding().setVariable(BR.item, itemList.get(position));
        holder.getBinding().setVariable(BR.vm, itemViewModel);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
