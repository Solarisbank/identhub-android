package de.solarisbank.identhub.verfication.phone.error;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.identity.IdentityModule;

public class VerificationPhoneErrorViewModelFactory implements Factory<ViewModel> {
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
