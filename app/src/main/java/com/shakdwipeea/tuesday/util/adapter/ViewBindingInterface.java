package com.shakdwipeea.tuesday.util.adapter;

import android.databinding.ViewDataBinding;

/**
 * Created by akash on 21/1/17.
 */

public interface ViewBindingInterface<V extends ViewDataBinding> {
    void bindView(V binding);
}
