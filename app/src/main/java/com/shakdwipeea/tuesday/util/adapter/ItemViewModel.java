package com.shakdwipeea.tuesday.util.adapter;

import android.databinding.ViewDataBinding;

/**
 * Created by akash on 27/1/17.
 */

public interface ItemViewModel<V extends ViewDataBinding> {
    /**
     * This method is used if you want to do some data binding stuff yourself
     *
     * @param binding DataBindingType that is generated for your layout
     */
    void bindDetail(V binding);
}
