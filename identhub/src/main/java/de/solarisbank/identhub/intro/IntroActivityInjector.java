package de.solarisbank.identhub.intro;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class IntroActivityInjector implements MembersInjector<IntroActivity> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public IntroActivityInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(IntroActivity instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(IntroActivity instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
