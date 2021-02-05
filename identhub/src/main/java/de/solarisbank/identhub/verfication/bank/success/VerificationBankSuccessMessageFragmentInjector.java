package de.solarisbank.identhub.verfication.bank.success;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class VerificationBankSuccessMessageFragmentInjector implements MembersInjector<VerificationBankSuccessMessageFragment> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public VerificationBankSuccessMessageFragmentInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(VerificationBankSuccessMessageFragment instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(VerificationBankSuccessMessageFragment instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}