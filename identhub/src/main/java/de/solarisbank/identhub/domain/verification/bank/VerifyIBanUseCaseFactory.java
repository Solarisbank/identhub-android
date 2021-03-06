package de.solarisbank.identhub.domain.verification.bank;

import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import de.solarisbank.sdk.core.di.internal.Provider;

public class VerifyIBanUseCaseFactory implements Factory<VerifyIBanUseCase> {

    private final Provider<VerificationBankRepository> verificationBankRepositoryProvider;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;

    public VerifyIBanUseCaseFactory(Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
                                    Provider<VerificationBankRepository> verificationBankRepositoryProvider) {
        this.verificationBankRepositoryProvider = verificationBankRepositoryProvider;
        this.getIdentificationUseCaseProvider = getIdentificationUseCaseProvider;
    }

    public static VerifyIBanUseCaseFactory create(Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
                                                  Provider<VerificationBankRepository> verificationBankRepositoryProvider) {
        return new VerifyIBanUseCaseFactory(getIdentificationUseCaseProvider, verificationBankRepositoryProvider);
    }

    @Override
    public VerifyIBanUseCase get() {
        return Preconditions.checkNotNull(
                new VerifyIBanUseCase(getIdentificationUseCaseProvider.get(), verificationBankRepositoryProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
