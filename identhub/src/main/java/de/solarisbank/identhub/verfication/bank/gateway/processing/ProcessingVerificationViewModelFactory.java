package de.solarisbank.identhub.verfication.bank.gateway.processing;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCase;
import de.solarisbank.identhub.verfication.bank.VerificationBankModule;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Provider;

public final class ProcessingVerificationViewModelFactory implements Factory<ViewModel> {
    private final VerificationBankModule verificationBankModule;
    private final Provider<ProcessingVerificationUseCase> processingVerificationUseCaseProvider;

    public ProcessingVerificationViewModelFactory(
            VerificationBankModule verificationBankModule,
            Provider<ProcessingVerificationUseCase> processingVerificationUseCaseProvider
    ) {
        this.verificationBankModule = verificationBankModule;
        this.processingVerificationUseCaseProvider = processingVerificationUseCaseProvider;
    }

    public static ProcessingVerificationViewModelFactory create(
            VerificationBankModule verificationBankModule,
            Provider<ProcessingVerificationUseCase> processingVerificationUseCaseProvider
    ) {
        return new ProcessingVerificationViewModelFactory(
                verificationBankModule,
                processingVerificationUseCaseProvider
        );
    }

    @Override
    public ProcessingVerificationViewModel get() {
        return verificationBankModule.provideProcessingVerificationViewModel(
                processingVerificationUseCaseProvider.get()
        );
    }
}
