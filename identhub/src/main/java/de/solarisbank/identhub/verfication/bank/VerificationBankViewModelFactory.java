package de.solarisbank.identhub.verfication.bank;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.di.internal.Provider;
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase;
import de.solarisbank.identhub.identity.IdentityModule;

public final class VerificationBankViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;
    private final Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider;

    public VerificationBankViewModelFactory(IdentityModule identityModule, Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider) {
        this.identityModule = identityModule;
        this.verifyIBanUseCaseProvider = verifyIBanUseCaseProvider;
    }

    public static VerificationBankViewModelFactory create(IdentityModule identityModule, Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider) {
        return new VerificationBankViewModelFactory(identityModule, verifyIBanUseCaseProvider);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideVerificationBankViewModel(verifyIBanUseCaseProvider.get());
    }
}
