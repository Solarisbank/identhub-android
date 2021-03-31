package de.solarisbank.identhub.verfication.phone.success;

import de.solarisbank.sdk.core.di.internal.MembersInjector;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory;

public final class VerificationPhoneSuccessMessageFragmentInjector implements MembersInjector<VerificationPhoneSuccessMessageFragment> {

    private final Provider<AssistedViewModelFactory> assistedViewModelFactory;

    public VerificationPhoneSuccessMessageFragmentInjector(Provider<AssistedViewModelFactory> assistedViewModelFactory) {
        this.assistedViewModelFactory = assistedViewModelFactory;
    }

    public static void injectAssistedViewModelFactory(VerificationPhoneSuccessMessageFragment instance, AssistedViewModelFactory assistedViewModelFactory) {
        instance.assistedViewModelFactory = assistedViewModelFactory;
    }

    @Override
    public void injectMembers(VerificationPhoneSuccessMessageFragment instance) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactory.get());
    }
}