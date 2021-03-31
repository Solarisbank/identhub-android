package de.solarisbank.identhub.verfication.phone;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.domain.verification.phone.AuthorizeVerificationPhoneUseCase;
import de.solarisbank.identhub.domain.verification.phone.ConfirmVerificationPhoneUseCase;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Provider;

public final class VerificationPhoneViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;
    private final Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider;
    private final Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider;

    public VerificationPhoneViewModelFactory(IdentityModule identityModule,
                                             Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider,
                                             Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider) {
        this.identityModule = identityModule;
        this.authorizeVerificationPhoneUseCaseProvider = authorizeVerificationPhoneUseCaseProvider;
        this.confirmVerificationPhoneUseCaseProvider = confirmVerificationPhoneUseCaseProvider;
    }

    public static VerificationPhoneViewModelFactory create(IdentityModule identityModule,
                                                           Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider,
                                                           Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider) {
        return new VerificationPhoneViewModelFactory(identityModule, authorizeVerificationPhoneUseCaseProvider, confirmVerificationPhoneUseCaseProvider);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideVerificationPhoneViewModel(authorizeVerificationPhoneUseCaseProvider.get(), confirmVerificationPhoneUseCaseProvider.get());
    }
}
