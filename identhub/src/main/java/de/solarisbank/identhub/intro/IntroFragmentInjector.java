package de.solarisbank.identhub.intro;

import de.solarisbank.sdk.core.di.internal.MembersInjector;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory;

public class IntroFragmentInjector implements MembersInjector<IntroFragment> {
    private final Provider<AssistedViewModelFactory> viewModelFactoryProvider;

    public IntroFragmentInjector(Provider<AssistedViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectAssistedViewModelFactory(IntroFragment instance, AssistedViewModelFactory saveStateViewModelFactory) {
        instance.assistedViewModelFactory = saveStateViewModelFactory;
    }

    @Override
    public void injectMembers(IntroFragment instance) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
