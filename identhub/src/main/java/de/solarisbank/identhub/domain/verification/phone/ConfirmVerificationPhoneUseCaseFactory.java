package de.solarisbank.identhub.domain.verification.phone;

import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;

public class ConfirmVerificationPhoneUseCaseFactory implements Factory<ConfirmVerificationPhoneUseCase> {

    private final Provider<VerificationPhoneRepository> verificationPhoneRepositoryProvider;

    public ConfirmVerificationPhoneUseCaseFactory(Provider<VerificationPhoneRepository> verificationPhoneRepositoryProvider) {
        this.verificationPhoneRepositoryProvider = verificationPhoneRepositoryProvider;
    }

    public static ConfirmVerificationPhoneUseCaseFactory create(Provider<VerificationPhoneRepository> verificationPhoneRepositoryProvider) {
        return new ConfirmVerificationPhoneUseCaseFactory(verificationPhoneRepositoryProvider);
    }

    @Override
    public ConfirmVerificationPhoneUseCase get() {
        return Preconditions.checkNotNull(
                new ConfirmVerificationPhoneUseCase(verificationPhoneRepositoryProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
