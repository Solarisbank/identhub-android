package de.solarisbank.identhub.verfication.phone.error;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.sdk.core.di.internal.Factory;

public final class VerificationPhoneErrorViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;

    public VerificationPhoneErrorViewModelFactory(IdentityModule identityModule) {
        this.identityModule = identityModule;
    }

    public static VerificationPhoneErrorViewModelFactory create(IdentityModule identityModule) {
        return new VerificationPhoneErrorViewModelFactory(identityModule);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideVerificationPhoneErrorViewModel();
    }
}
