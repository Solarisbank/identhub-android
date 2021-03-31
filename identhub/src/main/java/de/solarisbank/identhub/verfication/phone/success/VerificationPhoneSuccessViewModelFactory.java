package de.solarisbank.identhub.verfication.phone.success;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.sdk.core.di.internal.Factory;

public final class VerificationPhoneSuccessViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;

    public VerificationPhoneSuccessViewModelFactory(IdentityModule identityModule) {
        this.identityModule = identityModule;
    }

    public static VerificationPhoneSuccessViewModelFactory create(IdentityModule identityModule) {
        return new VerificationPhoneSuccessViewModelFactory(identityModule);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideVerificationPhoneSuccessViewModel();
    }
}
