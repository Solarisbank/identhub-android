package de.solarisbank.identhub.verfication.bank.gateway.processing;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.identhub.verfication.bank.VerificationBankModule;
import de.solarisbank.sdk.core.di.internal.Factory;

public final class ProcessingVerificationViewModelFactory implements Factory<ViewModel> {
    private final VerificationBankModule verificationBankModule;

    public ProcessingVerificationViewModelFactory(VerificationBankModule verificationBankModule) {
        this.verificationBankModule = verificationBankModule;
    }

    public static ProcessingVerificationViewModelFactory create(VerificationBankModule verificationBankModule) {
        return new ProcessingVerificationViewModelFactory(verificationBankModule);
    }

    @Override
    public ProcessingVerificationViewModel get() {
        return verificationBankModule.provideProcessingVerificationViewModel();
    }
}
