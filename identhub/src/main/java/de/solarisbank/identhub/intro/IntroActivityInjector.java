package de.solarisbank.identhub.intro;

import de.solarisbank.identhub.AssistedViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public final class IntroActivityInjector implements MembersInjector<IntroActivity> {

    private final Provider<AssistedViewModelFactory> viewModelFactoryProvider;

    public IntroActivityInjector(Provider<AssistedViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectAssistedViewModelFactory(IntroActivity instance, AssistedViewModelFactory saveStateViewModelFactory) {
        instance.assistedViewModelFactory = saveStateViewModelFactory;
    }

    @Override
    public void injectMembers(IntroActivity instance) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
