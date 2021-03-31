package de.solarisbank.identhub.verfication.bank;

import de.solarisbank.sdk.core.di.internal.MembersInjector;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory;

public final class VerificationBankFragmentInjector implements MembersInjector<VerificationBankFragment> {

    private final Provider<AssistedViewModelFactory> assistedViewModelFactory;

    public VerificationBankFragmentInjector(Provider<AssistedViewModelFactory> assistedViewModelFactory) {
        this.assistedViewModelFactory = assistedViewModelFactory;
    }

    public static void injectAssistedViewModelFactory(VerificationBankFragment instance, AssistedViewModelFactory assistedViewModelFactory) {
        instance.assistedViewModelFactory = assistedViewModelFactory;
    }

    @Override
    public void injectMembers(VerificationBankFragment instance) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactory.get());
    }
}
