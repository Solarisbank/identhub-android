package de.solarisbank.identhub.verfication.bank;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.identity.IdentityModule;

public class VerificationBankViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;

    public VerificationBankViewModelFactory(IdentityModule identityModule) {
        this.identityModule = identityModule;
    }

    public static VerificationBankViewModelFactory create(IdentityModule identityModule) {
        return new VerificationBankViewModelFactory(identityModule);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideVerificationBankViewModel();
    }
}
