package de.solarisbank.identhub.verfication.phone;

import de.solarisbank.sdk.core.di.internal.MembersInjector;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory;

public final class VerificationPhoneFragmentInjector implements MembersInjector<VerificationPhoneFragment> {

    private final Provider<AssistedViewModelFactory> assistedViewModelFactory;

    public VerificationPhoneFragmentInjector(Provider<AssistedViewModelFactory> assistedViewModelFactory) {
        this.assistedViewModelFactory = assistedViewModelFactory;
    }

    public static void injectAssistedViewModelFactory(VerificationPhoneFragment instance, AssistedViewModelFactory assistedViewModelFactory) {
        instance.assistedViewModelFactory = assistedViewModelFactory;
    }

    @Override
    public void injectMembers(VerificationPhoneFragment instance) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactory.get());
    }
}
