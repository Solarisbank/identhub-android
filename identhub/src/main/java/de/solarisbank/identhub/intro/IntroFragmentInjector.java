package de.solarisbank.identhub.intro;

import de.solarisbank.identhub.AssistedViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

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
