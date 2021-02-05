package de.solarisbank.identhub.verfication.bank;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class VerificationBankFragmentInjector implements MembersInjector<VerificationBankFragment> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public VerificationBankFragmentInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(VerificationBankFragment instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(VerificationBankFragment instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
