package de.solarisbank.identhub.identity.summary;

import de.solarisbank.sdk.core.di.internal.MembersInjector;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory;

public class IdentitySummaryFragmentInjector implements MembersInjector<IdentitySummaryFragment> {
    private final Provider<AssistedViewModelFactory> viewModelFactoryProvider;

    public IdentitySummaryFragmentInjector(Provider<AssistedViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectAssistedViewModelFactory(IdentitySummaryFragment instance, AssistedViewModelFactory saveStateViewModelFactory) {
        instance.assistedViewModelFactory = saveStateViewModelFactory;
    }

    @Override
    public void injectMembers(IdentitySummaryFragment instance) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
