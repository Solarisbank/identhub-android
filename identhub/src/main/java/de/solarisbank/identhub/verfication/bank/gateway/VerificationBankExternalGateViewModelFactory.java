package de.solarisbank.identhub.verfication.bank.gateway;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.verfication.bank.VerificationBankModule;
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource;
import de.solarisbank.sdk.feature.di.internal.Factory2;
import de.solarisbank.sdk.feature.di.internal.Provider;

public final class VerificationBankExternalGateViewModelFactory implements Factory2<ViewModel, SavedStateHandle> {
    private final VerificationBankModule verificationBankModule;
    private final Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider;
    private final Provider<IdentificationLocalDataSource> identificationLocalDataSource;

    public VerificationBankExternalGateViewModelFactory(VerificationBankModule verificationBankModule,
                                                        Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
                                                        Provider<IdentificationLocalDataSource> identificationLocalDataSource) {
        this.verificationBankModule = verificationBankModule;
        this.fetchingAuthorizedIBanStatusUseCaseProvider = fetchingAuthorizedIBanStatusUseCaseProvider;
        this.identificationLocalDataSource = identificationLocalDataSource;
    }

    public static VerificationBankExternalGateViewModelFactory create(VerificationBankModule verificationBankModule,
                                                                      Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
                                                                      Provider<IdentificationLocalDataSource> getIdentificationUseCaseProvider) {
        return new VerificationBankExternalGateViewModelFactory(verificationBankModule, fetchingAuthorizedIBanStatusUseCaseProvider, getIdentificationUseCaseProvider);
    }

    @Override
    public VerificationBankExternalGateViewModel create(SavedStateHandle value) {
        return verificationBankModule.provideVerificationBankExternalGateViewModel(value, fetchingAuthorizedIBanStatusUseCaseProvider.get(), identificationLocalDataSource.get());
    }
}
