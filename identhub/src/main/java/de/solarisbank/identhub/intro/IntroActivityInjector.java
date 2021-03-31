package de.solarisbank.identhub.intro;


import de.solarisbank.sdk.core.di.internal.MembersInjector;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory;

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
