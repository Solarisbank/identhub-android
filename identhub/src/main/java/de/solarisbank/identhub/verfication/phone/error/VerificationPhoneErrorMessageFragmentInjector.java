package de.solarisbank.identhub.verfication.phone.error;

import de.solarisbank.sdk.core.di.internal.MembersInjector;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory;

public final class VerificationPhoneErrorMessageFragmentInjector implements MembersInjector<VerificationPhoneErrorMessageFragment> {

    private final Provider<AssistedViewModelFactory> assistedViewModelFactory;

    public VerificationPhoneErrorMessageFragmentInjector(Provider<AssistedViewModelFactory> assistedViewModelFactory) {
        this.assistedViewModelFactory = assistedViewModelFactory;
    }

    public static void injectAssistedViewModelFactory(VerificationPhoneErrorMessageFragment instance, AssistedViewModelFactory assistedViewModelFactory) {
        instance.assistedViewModelFactory = assistedViewModelFactory;
    }

    @Override
    public void injectMembers(VerificationPhoneErrorMessageFragment instance) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactory.get());
    }
}