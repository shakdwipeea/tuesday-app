package com.shakdwipeea.tuesday.setup.details;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProviderDetailsActivityFragment extends Fragment {

    public ProviderDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_details, container, false);
    }
}
