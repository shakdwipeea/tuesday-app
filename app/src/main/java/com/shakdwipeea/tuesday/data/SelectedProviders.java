package com.shakdwipeea.tuesday.data;

import com.shakdwipeea.tuesday.data.entities.Provider;

import java.util.List;

/**
 * Created by ashak on 08-11-2016.
 */

public class SelectedProviders {
    private static List<Provider> providerList;

    public static List<Provider> getProviderList() {
        return providerList;
    }

    public static void setProviderList(List<Provider> providerList) {
        SelectedProviders.providerList = providerList;
    }
}
