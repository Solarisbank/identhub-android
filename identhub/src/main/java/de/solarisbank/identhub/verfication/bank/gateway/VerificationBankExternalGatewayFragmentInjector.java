package de.solarisbank.identhub.verfication.bank.gateway;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class VerificationBankExternalGatewayFragmentInjector implements MembersInjector<VerificationBankExternalGatewayFragment> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public VerificationBankExternalGatewayFragmentInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(VerificationBankExternalGatewayFragment instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(VerificationBankExternalGatewayFragment instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
