package de.solarisbank.identhub.verfication.bank.gateway.processing;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.domain.verification.bank.JointAccountBankIdPostUseCase;
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase;
import de.solarisbank.identhub.verfication.bank.VerificationBankModule;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Provider;

public final class ProcessingVerificationViewModelFactory implements Factory<ViewModel> {
    private final VerificationBankModule verificationBankModule;
    private final Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider;
    private final Provider<JointAccountBankIdPostUseCase> bankIdPostUseCaseProvider;

    public ProcessingVerificationViewModelFactory(
            VerificationBankModule verificationBankModule,
            Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider,
            Provider<JointAccountBankIdPostUseCase> bankIdPostUseCaseProvider
    ) {
        this.verificationBankModule = verificationBankModule;
        this.identificationPollingStatusUseCaseProvider = identificationPollingStatusUseCaseProvider;
        this.bankIdPostUseCaseProvider = bankIdPostUseCaseProvider;
    }

    public static ProcessingVerificationViewModelFactory create(
            VerificationBankModule verificationBankModule,
            Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider,
            Provider<JointAccountBankIdPostUseCase> bankIdPostUseCaseProvider
    ) {
        return new ProcessingVerificationViewModelFactory(
                verificationBankModule,
                identificationPollingStatusUseCaseProvider,
                bankIdPostUseCaseProvider
        );
    }

    @Override
    public ProcessingVerificationViewModel get() {
        return verificationBankModule.provideProcessingVerificationViewModel(
                identificationPollingStatusUseCaseProvider.get(),
                bankIdPostUseCaseProvider.get()
        );
    }
}
