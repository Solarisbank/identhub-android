package de.solarisbank.identhub.verfication.phone;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.identity.IdentityModule;

public class VerificationPhoneViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;

    public VerificationPhoneViewModelFactory(IdentityModule identityModule) {
        this.identityModule = identityModule;
    }

    public static VerificationPhoneViewModelFactory create(IdentityModule identityModule) {
        return new VerificationPhoneViewModelFactory(identityModule);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideVerificationPhoneViewModel();
    }
}
