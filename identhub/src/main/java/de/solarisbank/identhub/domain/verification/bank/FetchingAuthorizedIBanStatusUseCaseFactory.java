package de.solarisbank.identhub.domain.verification.bank;

import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;

public class FetchingAuthorizedIBanStatusUseCaseFactory implements Factory<FetchingAuthorizedIBanStatusUseCase> {

    private final Provider<VerificationBankRepository> verificationBankRepositoryProvider;

    public FetchingAuthorizedIBanStatusUseCaseFactory(
            Provider<VerificationBankRepository> verificationBankRepositoryProvider
    ) {
        this.verificationBankRepositoryProvider = verificationBankRepositoryProvider;
    }

    public static FetchingAuthorizedIBanStatusUseCaseFactory create(
            Provider<VerificationBankRepository> verificationBankRepositoryProvider
    ) {
        return new FetchingAuthorizedIBanStatusUseCaseFactory(verificationBankRepositoryProvider);
    }

    @Override
    public FetchingAuthorizedIBanStatusUseCase get() {
        return Preconditions.checkNotNull(
                new FetchingAuthorizedIBanStatusUseCase(verificationBankRepositoryProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
