package de.solarisbank.identhub.di;

import androidx.annotation.RestrictTo;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.LinkedHashMap;
import java.util.Map;

import de.solarisbank.identhub.contract.sign.ContractSigningViewModel;
import de.solarisbank.identhub.contract.sign.ContractSigningViewModelFactory;
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.di.internal.Factory2;
import de.solarisbank.identhub.di.internal.Provider;
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase;
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase;
import de.solarisbank.identhub.domain.contract.DeleteAllLocalStorageUseCase;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.session.SessionUrlRepository;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.fourthline.FourthlineModule;
import de.solarisbank.identhub.fourthline.selfie.SelfieSharedViewModel;
import de.solarisbank.identhub.fourthline.selfie.SelfieSharedViewModelFactory;
import de.solarisbank.identhub.fourthline.terms.TermsAndConditionsModelFactory;
import de.solarisbank.identhub.fourthline.terms.TermsAndConditionsViewModel;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.identhub.identity.summary.IdentitySummaryFragmentViewModel;
import de.solarisbank.identhub.identity.summary.IdentitySummaryFragmentViewModelFactory;
import de.solarisbank.identhub.identity.summary.IdentitySummaryViewModel;
import de.solarisbank.identhub.identity.summary.IdentitySummaryViewModelFactory;
import de.solarisbank.identhub.intro.IntroActivityViewModel;
import de.solarisbank.identhub.intro.IntroActivityViewModelFactory;
import de.solarisbank.identhub.intro.IntroModule;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModel;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModelFactory;

@RestrictTo(RestrictTo.Scope.LIBRARY)
final class SaveStateViewModelMapProvider implements Provider<Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>>> {

    private final IntroModule introModule;
    private final IdentityModule identityModule;
    private final FourthlineModule fourthlineModule;
    private final Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider;
    private final Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider;
    private final Provider<DeleteAllLocalStorageUseCase> deleteAllLocalStorageUseCaseProvider;
    private final Provider<GetDocumentsUseCase> getDocumentsUseCaseProvider;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;
    private final Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider;
    private final Provider<FetchPdfUseCase> fetchPdfUseCaseProvider;
    private final Provider<IdentificationStepPreferences> identificationStepPreferencesProvider;
    private final Provider<SessionUrlRepository> sessionUrlRepositoryProvider;

    public SaveStateViewModelMapProvider(Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider,
                                         Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider,
                                         Provider<DeleteAllLocalStorageUseCase> deleteAllLocalStorageUseCaseProvider,
                                         Provider<GetDocumentsUseCase> getDocumentsUseCaseProvider,
                                         Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
                                         Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
                                         Provider<FetchPdfUseCase> fetchPdfUseCaseProvider,
                                         IdentityModule identityModule,
                                         Provider<IdentificationStepPreferences> identificationStepPreferencesProvider,
                                         Provider<SessionUrlRepository> sessionUrlRepositoryProvider,
                                         IntroModule introModule,
                                         FourthlineModule fourthlineModule) {
        this.authorizeContractSignUseCaseProvider = authorizeContractSignUseCaseProvider;
        this.confirmContractSignUseCaseProvider = confirmContractSignUseCaseProvider;
        this.deleteAllLocalStorageUseCaseProvider = deleteAllLocalStorageUseCaseProvider;
        this.getDocumentsUseCaseProvider = getDocumentsUseCaseProvider;
        this.getIdentificationUseCaseProvider = getIdentificationUseCaseProvider;
        this.fetchPdfUseCaseProvider = fetchPdfUseCaseProvider;
        this.fetchingAuthorizedIBanStatusUseCaseProvider = fetchingAuthorizedIBanStatusUseCaseProvider;
        this.identityModule = identityModule;
        this.introModule = introModule;
        this.fourthlineModule = fourthlineModule;
        this.identificationStepPreferencesProvider = identificationStepPreferencesProvider;
        this.sessionUrlRepositoryProvider = sessionUrlRepositoryProvider;
    }

    public static SaveStateViewModelMapProvider create(
            Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider,
            Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider,
            Provider<DeleteAllLocalStorageUseCase> deleteAllLocalStorageUseCaseProvider,
            Provider<FetchPdfUseCase> fetchPdfUseCaseProvider,
            Provider<GetDocumentsUseCase> getDocumentsUseCaseProvider,
            Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
            Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
            IdentityModule identityModule,
            Provider<IdentificationStepPreferences> identificationStepPreferencesProvider,
            Provider<SessionUrlRepository> sessionUrlRepositoryProvider,
            IntroModule introModule,
            FourthlineModule fourthlineModule) {
        return new SaveStateViewModelMapProvider(
                authorizeContractSignUseCaseProvider,
                confirmContractSignUseCaseProvider,
                deleteAllLocalStorageUseCaseProvider,
                getDocumentsUseCaseProvider,
                getIdentificationUseCaseProvider,
                fetchingAuthorizedIBanStatusUseCaseProvider,
                fetchPdfUseCaseProvider,
                identityModule,
                identificationStepPreferencesProvider,
                sessionUrlRepositoryProvider,
                introModule,
                fourthlineModule);
    }

    @Override
    public Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>> get() {
        Map<Class<? extends ViewModel>, Factory2<ViewModel, SavedStateHandle>> map = new LinkedHashMap<>(3);

        map.put(ContractSigningViewModel.class, ContractSigningViewModelFactory.create(identityModule, authorizeContractSignUseCaseProvider, confirmContractSignUseCaseProvider, getIdentificationUseCaseProvider));
        map.put(IntroActivityViewModel.class, IntroActivityViewModelFactory.create(introModule, identificationStepPreferencesProvider, sessionUrlRepositoryProvider));
        map.put(VerificationBankExternalGateViewModel.class, VerificationBankExternalGateViewModelFactory.create(identityModule, fetchingAuthorizedIBanStatusUseCaseProvider, getIdentificationUseCaseProvider));
        map.put(IdentitySummaryFragmentViewModel.class, IdentitySummaryFragmentViewModelFactory.create(identityModule, deleteAllLocalStorageUseCaseProvider, getDocumentsUseCaseProvider, fetchPdfUseCaseProvider, identificationStepPreferencesProvider));
        map.put(IdentitySummaryViewModel.class, IdentitySummaryViewModelFactory.create(identityModule, getIdentificationUseCaseProvider));
        map.put(TermsAndConditionsViewModel.class, TermsAndConditionsModelFactory.create(fourthlineModule));
        map.put(SelfieSharedViewModel.class, SelfieSharedViewModelFactory.create(fourthlineModule));

        return map;
    }
}
