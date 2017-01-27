package com.shakdwipeea.tuesday.util.adapter;

import android.databinding.ViewDataBinding;

/**
 * Created by akash on 27/1/17.
 */

public interface ItemViewModel<V extends ViewDataBinding, E> {
    /**
     * This method is used if you want to do some data binding stuff yourself
     *
     * @param binding DataBindingType that is generated for your layout
     * @param item Item for which rendering is done
     * @param position Position of the item in the list
     */
    void bindDetail(V binding, E item, int position);
}
