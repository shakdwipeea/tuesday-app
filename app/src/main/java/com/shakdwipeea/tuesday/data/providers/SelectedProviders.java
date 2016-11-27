package com.shakdwipeea.tuesday.data.providers;

import com.shakdwipeea.tuesday.data.entities.Provider;

import java.util.List;

import rx.Observable;

/**
 * Created by ashak on 08-11-2016.
 */

public class SelectedProviders {
    private static SelectedProviders selectedProviders;

    private Observable<List<Provider>> providerObservable;

    public static SelectedProviders getInstance() {
        if (selectedProviders == null) selectedProviders = new SelectedProviders();

        return selectedProviders;
    }

    public Observable<List<Provider>> getProviderList() {
        return providerObservable;
    }

    public void setProviderList(Observable<List<Provider>> providerObservable) {
        this.providerObservable = providerObservable;
    }

    public Observable<Provider> getProvider(String name) {
        return getProviderList()
                .flatMapIterable(providers -> providers)
                .filter(provider -> provider.getName().equals(name));
    }

    public Observable<Provider> getProvider(int index) {
        return getProviderList()
                .flatMapIterable(providers -> providers)
                .elementAt(index);
    }

}
