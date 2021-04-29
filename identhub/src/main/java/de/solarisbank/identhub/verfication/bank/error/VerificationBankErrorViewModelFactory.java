package de.solarisbank.identhub.verfication.bank.error;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.verfication.bank.VerificationBankModule;
import de.solarisbank.sdk.core.di.internal.Factory;

public final class VerificationBankErrorViewModelFactory implements Factory<ViewModel> {
    private final VerificationBankModule verificationBankModule;

    public VerificationBankErrorViewModelFactory(VerificationBankModule verificationBankModule) {
        this.verificationBankModule = verificationBankModule;
    }

    public static VerificationBankErrorViewModelFactory create(VerificationBankModule verificationBankModule) {
        return new VerificationBankErrorViewModelFactory(verificationBankModule);
    }

    @Override
    public ViewModel get() {
        return verificationBankModule.provideVerificationBankErrorViewModel();
    }
}
