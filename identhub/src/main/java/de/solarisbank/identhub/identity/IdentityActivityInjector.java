package de.solarisbank.identhub.identity;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class IdentityActivityInjector implements MembersInjector<IdentityActivity> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public IdentityActivityInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(IdentityActivity instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(IdentityActivity instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
