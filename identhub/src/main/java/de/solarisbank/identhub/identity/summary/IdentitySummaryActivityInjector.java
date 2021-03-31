package de.solarisbank.identhub.identity.summary;

import de.solarisbank.sdk.core.di.internal.MembersInjector;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory;

public class IdentitySummaryActivityInjector implements MembersInjector<IdentitySummaryActivity> {
    private final Provider<AssistedViewModelFactory> viewModelFactoryProvider;

    public IdentitySummaryActivityInjector(Provider<AssistedViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectAssistedViewModelFactory(IdentitySummaryActivity instance, AssistedViewModelFactory saveStateViewModelFactory) {
        instance.assistedViewModelFactory = saveStateViewModelFactory;
    }

    @Override
    public void injectMembers(IdentitySummaryActivity instance) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
