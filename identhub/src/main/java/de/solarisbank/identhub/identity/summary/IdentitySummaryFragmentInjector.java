package de.solarisbank.identhub.identity.summary;

import de.solarisbank.identhub.AssistedViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

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
