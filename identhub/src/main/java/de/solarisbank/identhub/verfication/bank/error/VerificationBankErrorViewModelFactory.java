package de.solarisbank.identhub.verfication.bank.error;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.identity.IdentityModule;

public final class VerificationBankErrorViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;

    public VerificationBankErrorViewModelFactory(IdentityModule identityModule) {
        this.identityModule = identityModule;
    }

    public static VerificationBankErrorViewModelFactory create(IdentityModule identityModule) {
        return new VerificationBankErrorViewModelFactory(identityModule);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideVerificationBankErrorViewModel();
    }
}
