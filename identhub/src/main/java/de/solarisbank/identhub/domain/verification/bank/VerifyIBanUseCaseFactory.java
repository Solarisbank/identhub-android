package de.solarisbank.identhub.domain.verification.bank;

import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;

public class VerifyIBanUseCaseFactory implements Factory<VerifyIBanUseCase> {

    private final Provider<VerificationBankRepository> verificationBankRepositoryProvider;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;
    private final Provider<IdentityInitializationRepository> identityInitializationRepositoryProvider;

    public VerifyIBanUseCaseFactory(
            Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
            Provider<VerificationBankRepository> verificationBankRepositoryProvider,
            Provider<IdentityInitializationRepository> identityInitializationRepositoryProvider
            ) {
        this.verificationBankRepositoryProvider = verificationBankRepositoryProvider;
        this.getIdentificationUseCaseProvider = getIdentificationUseCaseProvider;
        this.identityInitializationRepositoryProvider = identityInitializationRepositoryProvider;
    }

    public static VerifyIBanUseCaseFactory create(
            Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
            Provider<VerificationBankRepository> verificationBankRepositoryProvider,
            Provider<IdentityInitializationRepository> identityInitializationRepositoryProvider
            ) {
        return new VerifyIBanUseCaseFactory(
                getIdentificationUseCaseProvider,
                verificationBankRepositoryProvider,
                identityInitializationRepositoryProvider
                );
    }

    @Override
    public VerifyIBanUseCase get() {
        return Preconditions.checkNotNull(
                new VerifyIBanUseCase(
                        getIdentificationUseCaseProvider.get(),
                        verificationBankRepositoryProvider.get(),
                        identityInitializationRepositoryProvider.get()
                ),
                "Cannot return null from provider method"
        );
    }
}
