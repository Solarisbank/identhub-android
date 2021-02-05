package de.solarisbank.identhub.verfication.phone.success;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class VerificationPhoneSuccessMessageFragmentInjector implements MembersInjector<VerificationPhoneSuccessMessageFragment> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public VerificationPhoneSuccessMessageFragmentInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(VerificationPhoneSuccessMessageFragment instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(VerificationPhoneSuccessMessageFragment instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}