package de.solarisbank.identhub.verfication.phone.error;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class VerificationPhoneErrorMessageFragmentInjector implements MembersInjector<VerificationPhoneErrorMessageFragment> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public VerificationPhoneErrorMessageFragmentInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(VerificationPhoneErrorMessageFragment instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(VerificationPhoneErrorMessageFragment instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}