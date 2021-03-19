package de.solarisbank.identhub.verfication.bank.success;

import de.solarisbank.identhub.AssistedViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public final class VerificationBankSuccessMessageFragmentInjector implements MembersInjector<VerificationBankSuccessMessageFragment> {

    private final Provider<AssistedViewModelFactory> assistedViewModelFactory;

    public VerificationBankSuccessMessageFragmentInjector(Provider<AssistedViewModelFactory> assistedViewModelFactory) {
        this.assistedViewModelFactory = assistedViewModelFactory;
    }

    public static void injectAssistedViewModelFactory(VerificationBankSuccessMessageFragment instance, AssistedViewModelFactory assistedViewModelFactory) {
        instance.assistedViewModelFactory = assistedViewModelFactory;
    }

    @Override
    public void injectMembers(VerificationBankSuccessMessageFragment instance) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactory.get());
    }
}