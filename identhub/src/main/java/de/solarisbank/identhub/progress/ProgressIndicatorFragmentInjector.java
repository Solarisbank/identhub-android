package de.solarisbank.identhub.progress;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class ProgressIndicatorFragmentInjector implements MembersInjector<ProgressIndicatorFragment> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public ProgressIndicatorFragmentInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(ProgressIndicatorFragment instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(ProgressIndicatorFragment instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
