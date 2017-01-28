package com.shakdwipeea.tuesday.home;

/**
 * Created by akash on 28/1/17.
 */

public interface FragmentViewPagerLifeCycle {
    /**
     * Called when fragment is not foreground
     */
    void onPauseFragment();

    /**
     * Called when fragment comes foreground
     */
    void onResumeFragment();
}
