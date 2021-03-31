package de.solarisbank.identhub.domain.verification.phone;

import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import de.solarisbank.sdk.core.di.internal.Provider;

public class AuthorizeVerificationPhoneUseCaseFactory implements Factory<AuthorizeVerificationPhoneUseCase> {

    private final Provider<VerificationPhoneRepository> verificationPhoneRepositoryProvider;

    public AuthorizeVerificationPhoneUseCaseFactory(Provider<VerificationPhoneRepository> verificationPhoneRepositoryProvider) {
        this.verificationPhoneRepositoryProvider = verificationPhoneRepositoryProvider;
    }

    public static AuthorizeVerificationPhoneUseCaseFactory create(Provider<VerificationPhoneRepository> verificationPhoneRepositoryProvider) {
        return new AuthorizeVerificationPhoneUseCaseFactory(verificationPhoneRepositoryProvider);
    }

    @Override
    public AuthorizeVerificationPhoneUseCase get() {
        return Preconditions.checkNotNull(
                new AuthorizeVerificationPhoneUseCase(verificationPhoneRepositoryProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
