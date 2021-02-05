package de.solarisbank.identhub.verfication.bank.success;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.identity.IdentityModule;

public class VerificationBankSuccessViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;

    public VerificationBankSuccessViewModelFactory(IdentityModule identityModule) {
        this.identityModule = identityModule;
    }

    public static VerificationBankSuccessViewModelFactory create(IdentityModule identityModule) {
        return new VerificationBankSuccessViewModelFactory(identityModule);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideVerificationBankSuccessViewModel();
    }
}
