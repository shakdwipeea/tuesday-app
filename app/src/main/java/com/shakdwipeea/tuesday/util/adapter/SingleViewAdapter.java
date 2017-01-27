package com.shakdwipeea.tuesday.util.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.BR;

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
 * @param <ViewBindingType> DataBinding type
 */
public class SingleViewAdapter<ItemType,
        ItemViewModelType extends ItemViewModel<ViewBindingType, ItemType>,
        ViewBindingType extends ViewDataBinding>
        extends RecyclerView.Adapter<SingleViewHolder<ViewBindingType>> {
    private static final String TAG = "SingleViewAdapter";

    private ItemViewModelType itemViewModel;
    private List<ItemType> itemList = new ArrayList<>();

    private int layoutId;

    public SingleViewAdapter(ItemViewModelType itemViewModel, int layoutId) {
        this.itemViewModel = itemViewModel;
        this.layoutId = layoutId;
    }

    public List<ItemType> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemType> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public void addItem(ItemType item) {
        this.itemList.add(item);
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.itemList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public SingleViewHolder<ViewBindingType> onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewBindingType binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), layoutId, parent, false
        );

        return new SingleViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(SingleViewHolder<ViewBindingType> holder, int position) {
        Log.d(TAG, "onBindViewHolder: Here " + itemViewModel) ;
        holder.getBinding().setVariable(BR.item, itemList.get(position));
        holder.getBinding().setVariable(BR.vm, itemViewModel);
        itemViewModel.bindDetail(holder.getBinding(), itemList.get(position), position);
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
