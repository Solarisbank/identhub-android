package de.solarisbank.identhub.verfication.bank.gateway;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory2;
import de.solarisbank.identhub.di.internal.Provider;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.identity.IdentityModule;

public final class VerificationBankExternalGateViewModelFactory implements Factory2<ViewModel, SavedStateHandle> {
    private final IdentityModule identityModule;
    private final Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;

    public VerificationBankExternalGateViewModelFactory(IdentityModule identityModule,
                                                        Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
                                                        Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider) {
        this.identityModule = identityModule;
        this.fetchingAuthorizedIBanStatusUseCaseProvider = fetchingAuthorizedIBanStatusUseCaseProvider;
        this.getIdentificationUseCaseProvider = getIdentificationUseCaseProvider;
    }

    public static VerificationBankExternalGateViewModelFactory create(IdentityModule identityModule,
                                                                      Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
                                                                      Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider) {
        return new VerificationBankExternalGateViewModelFactory(identityModule, fetchingAuthorizedIBanStatusUseCaseProvider, getIdentificationUseCaseProvider);
    }

    @Override
    public VerificationBankExternalGateViewModel create(SavedStateHandle value) {
        return identityModule.provideVerificationBankExternalGateViewModel(value, fetchingAuthorizedIBanStatusUseCaseProvider.get(), getIdentificationUseCaseProvider.get());
    }
}
