package de.solarisbank.identhub.verfication.bank.gateway;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.verfication.bank.VerificationBankModule;
import de.solarisbank.sdk.feature.di.internal.Factory2;
import de.solarisbank.sdk.feature.di.internal.Provider;

public final class VerificationBankExternalGateViewModelFactory implements Factory2<ViewModel, SavedStateHandle> {
    private final VerificationBankModule verificationBankModule;
    private final Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;

    public VerificationBankExternalGateViewModelFactory(VerificationBankModule verificationBankModule,
                                                        Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
                                                        Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider) {
        this.verificationBankModule = verificationBankModule;
        this.fetchingAuthorizedIBanStatusUseCaseProvider = fetchingAuthorizedIBanStatusUseCaseProvider;
        this.getIdentificationUseCaseProvider = getIdentificationUseCaseProvider;
    }

    public static VerificationBankExternalGateViewModelFactory create(VerificationBankModule verificationBankModule,
                                                                      Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
                                                                      Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider) {
        return new VerificationBankExternalGateViewModelFactory(verificationBankModule, fetchingAuthorizedIBanStatusUseCaseProvider, getIdentificationUseCaseProvider);
    }

    @Override
    public VerificationBankExternalGateViewModel create(SavedStateHandle value) {
        return verificationBankModule.provideVerificationBankExternalGateViewModel(value, fetchingAuthorizedIBanStatusUseCaseProvider.get(), getIdentificationUseCaseProvider.get());
    }
}
