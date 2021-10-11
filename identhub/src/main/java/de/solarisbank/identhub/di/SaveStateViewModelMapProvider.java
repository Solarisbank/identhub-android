package de.solarisbank.identhub.di;

import androidx.annotation.RestrictTo;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.LinkedHashMap;
import java.util.Map;

import de.solarisbank.identhub.contract.ContractModule;
import de.solarisbank.identhub.contract.ContractViewModel;
import de.solarisbank.identhub.contract.ContractViewModelFactory;
import de.solarisbank.identhub.domain.contract.DeleteAllLocalStorageUseCase;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.verfication.bank.VerificationBankModule;
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModel;
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModelFactory;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModel;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModelFactory;
import de.solarisbank.sdk.data.repository.SessionUrlRepository;
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase;
import de.solarisbank.sdk.feature.config.InitializationInfoRepository;
import de.solarisbank.sdk.feature.di.internal.Factory2;
import de.solarisbank.sdk.feature.di.internal.Provider;

@RestrictTo(RestrictTo.Scope.LIBRARY)
final class SaveStateViewModelMapProvider implements Provider<Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>>> {

    private final IdentityModule identityModule;
    private final VerificationBankModule verificationBankModule;
    private final ContractModule contractModule;
    private final Provider<DeleteAllLocalStorageUseCase> deleteAllLocalStorageUseCaseProvider;
    private final Provider<GetDocumentsUseCase> getDocumentsUseCaseProvider;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;
    private final Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider;
    private final Provider<IdentificationStepPreferences> identificationStepPreferencesProvider;
    private final Provider<SessionUrlRepository> sessionUrlRepositoryProvider;
    private final Provider<InitializationInfoRepository> initializationInfoRepositoryProvider;

    public SaveStateViewModelMapProvider(
            Provider<DeleteAllLocalStorageUseCase> deleteAllLocalStorageUseCaseProvider,
            Provider<GetDocumentsUseCase> getDocumentsUseCaseProvider,
            Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
            Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
            Provider<InitializationInfoRepository> initializationInfoRepositoryProvider,
            IdentityModule identityModule,
            Provider<IdentificationStepPreferences> identificationStepPreferencesProvider,
            Provider<SessionUrlRepository> sessionUrlRepositoryProvider,
            VerificationBankModule verificationBankModule,
            ContractModule contractModule
    ) {
        this.deleteAllLocalStorageUseCaseProvider = deleteAllLocalStorageUseCaseProvider;
        this.getDocumentsUseCaseProvider = getDocumentsUseCaseProvider;
        this.getIdentificationUseCaseProvider = getIdentificationUseCaseProvider;
        this.fetchingAuthorizedIBanStatusUseCaseProvider = fetchingAuthorizedIBanStatusUseCaseProvider;
        this.identityModule = identityModule;
        this.identificationStepPreferencesProvider = identificationStepPreferencesProvider;
        this.sessionUrlRepositoryProvider = sessionUrlRepositoryProvider;
        this.verificationBankModule = verificationBankModule;
        this.contractModule = contractModule;
        this.initializationInfoRepositoryProvider = initializationInfoRepositoryProvider;
    }

    @Override
    public Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>> get() {
        Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>> map = new LinkedHashMap<>(3);

        map.put(VerificationBankExternalGateViewModel.class, VerificationBankExternalGateViewModelFactory.create(
                verificationBankModule,
                fetchingAuthorizedIBanStatusUseCaseProvider,
                getIdentificationUseCaseProvider
        ));
        map.put(VerificationBankViewModel.class, new VerificationBankViewModelFactory(
                verificationBankModule,
                identificationStepPreferencesProvider,
                getIdentificationUseCaseProvider,
                sessionUrlRepositoryProvider,
                initializationInfoRepositoryProvider
        ));
        map.put(ContractViewModel.class, ContractViewModelFactory.create(
                contractModule,
                identificationStepPreferencesProvider,
                sessionUrlRepositoryProvider,
                getIdentificationUseCaseProvider
        ));

        return map;
    }
}
