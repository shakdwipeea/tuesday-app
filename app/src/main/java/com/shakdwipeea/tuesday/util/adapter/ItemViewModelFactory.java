package com.shakdwipeea.tuesday.util.adapter;

/**
 * Created by akash on 28/1/17.
 */

public interface ItemViewModelFactory <VM, E> {
    VM newInstance(E item);
}
