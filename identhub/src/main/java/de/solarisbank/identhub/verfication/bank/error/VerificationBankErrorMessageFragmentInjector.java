package de.solarisbank.identhub.verfication.bank.error;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class VerificationBankErrorMessageFragmentInjector implements MembersInjector<VerificationBankErrorMessageFragment> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public VerificationBankErrorMessageFragmentInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(VerificationBankErrorMessageFragment instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(VerificationBankErrorMessageFragment instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}