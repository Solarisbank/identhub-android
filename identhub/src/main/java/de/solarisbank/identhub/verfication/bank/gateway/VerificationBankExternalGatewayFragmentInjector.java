package de.solarisbank.identhub.verfication.bank.gateway;

import de.solarisbank.sdk.core.di.internal.MembersInjector;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory;

public final class VerificationBankExternalGatewayFragmentInjector implements MembersInjector<VerificationBankExternalGatewayFragment> {

    private final Provider<AssistedViewModelFactory> assistedViewModelFactory;

    public VerificationBankExternalGatewayFragmentInjector(Provider<AssistedViewModelFactory> assistedViewModelFactory) {
        this.assistedViewModelFactory = assistedViewModelFactory;
    }

    public static void injectAssistedViewModelFactory(VerificationBankExternalGatewayFragment instance, AssistedViewModelFactory assistedViewModelFactory) {
        instance.assistedViewModelFactory = assistedViewModelFactory;
    }

    @Override
    public void injectMembers(VerificationBankExternalGatewayFragment instance) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactory.get());
    }
}
