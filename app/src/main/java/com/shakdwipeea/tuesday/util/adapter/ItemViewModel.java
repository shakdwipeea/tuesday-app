package com.shakdwipeea.tuesday.util.adapter;

import android.databinding.ViewDataBinding;

/**
 * Created by akash on 27/1/17.
 */

public interface ItemViewModel<V extends ViewDataBinding, E> {
    void bindDetail(V binding, E item);
}
