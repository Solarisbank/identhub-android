package de.solarisbank.identhub.di;

import androidx.annotation.RestrictTo;
import androidx.lifecycle.ViewModel;

import java.util.LinkedHashMap;
import java.util.Map;

import de.solarisbank.identhub.contract.ContractModule;
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel;
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModelFactory;
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.domain.verification.bank.JointAccountBankIdPostUseCase;
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCase;
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase;
import de.solarisbank.identhub.domain.verification.phone.AuthorizeVerificationPhoneUseCase;
import de.solarisbank.identhub.domain.verification.phone.ConfirmVerificationPhoneUseCase;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;
import de.solarisbank.identhub.identity.IdentityActivityViewModelFactory;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase;
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModel;
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModelFactory;
import de.solarisbank.identhub.verfication.bank.VerificationBankModule;
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel;
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModelFactory;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModel;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModelFactory;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorViewModel;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorViewModelFactory;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModel;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModelFactory;
import de.solarisbank.sdk.core.di.internal.Provider;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ViewModelMapProvider implements Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> {

    private final IdentityModule identityModule;
    private final VerificationBankModule verificationBankModule;
    private final ContractModule contractModule;

    private final Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider;
    private final Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider;
    private final Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;
    private final Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider;
    private final Provider<FetchPdfUseCase> fetchPdfUseCaseProvider;
    private final Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider;
    private final Provider<IdentificationStepPreferences> identificationStepPreferencesProvider;
    private final Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider;
    private final Provider<JointAccountBankIdPostUseCase> bankIdPostUseCaseProvider;
    private final Provider<ProcessingVerificationUseCase> processingVerificationUseCaseProvider;

    public ViewModelMapProvider(
            IdentityModule identityModule,
            VerificationBankModule verificationBankModule,
            ContractModule contractModule,
            Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider,
            Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider,
            Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider,
            Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
            Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
            Provider<FetchPdfUseCase> fetchPdfUseCaseProvider,
            Provider<IdentificationStepPreferences> identificationStepPreferencesProvider,
            Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider,
            Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider,
            Provider<JointAccountBankIdPostUseCase> bankIdPostUseCaseProvider,
            Provider<ProcessingVerificationUseCase> processingVerificationUseCaseProvider
    ) {
        this.identityModule = identityModule;
        this.verificationBankModule = verificationBankModule;
        this.contractModule = contractModule;
        this.authorizeVerificationPhoneUseCaseProvider = authorizeVerificationPhoneUseCaseProvider;
        this.confirmVerificationPhoneUseCaseProvider = confirmVerificationPhoneUseCaseProvider;
        this.getDocumentsFromVerificationBankUseCaseProvider = getDocumentsFromVerificationBankUseCaseProvider;
        this.getIdentificationUseCaseProvider = getIdentificationUseCaseProvider;
        this.fetchingAuthorizedIBanStatusUseCaseProvider = fetchingAuthorizedIBanStatusUseCaseProvider;
        this.fetchPdfUseCaseProvider = fetchPdfUseCaseProvider;
        this.identificationStepPreferencesProvider = identificationStepPreferencesProvider;
        this.verifyIBanUseCaseProvider = verifyIBanUseCaseProvider;
        this.identificationPollingStatusUseCaseProvider = identificationPollingStatusUseCaseProvider;
        this.bankIdPostUseCaseProvider = bankIdPostUseCaseProvider;
        this.processingVerificationUseCaseProvider = processingVerificationUseCaseProvider;
    }

    public static ViewModelMapProvider create(
            IdentityModule identityModule,
            VerificationBankModule verificationBankModule,
            ContractModule contractModule,
            Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider,
            Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider,
            Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider,
            Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
            Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
            Provider<FetchPdfUseCase> fetchPdfUseCaseProvider,
            Provider<IdentificationStepPreferences> identificationStepPreferencesProvider,
            Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider,
            Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider,
            Provider<JointAccountBankIdPostUseCase> bankIdPostUseCaseProvider,
            Provider<ProcessingVerificationUseCase> processingVerificationUseCaseProvider
    ) {
        return new ViewModelMapProvider(
                identityModule,
                verificationBankModule,
                contractModule,
                authorizeVerificationPhoneUseCaseProvider,
                confirmVerificationPhoneUseCaseProvider,
                getDocumentsFromVerificationBankUseCaseProvider,
                getIdentificationUseCaseProvider,
                fetchingAuthorizedIBanStatusUseCaseProvider,
                fetchPdfUseCaseProvider,
                identificationStepPreferencesProvider,
                verifyIBanUseCaseProvider,
                identificationPollingStatusUseCaseProvider,
                bankIdPostUseCaseProvider,
                processingVerificationUseCaseProvider
        );
    }

    @Override
    public Map<Class<? extends ViewModel>, Provider<ViewModel>> get() {
        Map<Class<? extends ViewModel>, Provider<ViewModel>> map = new LinkedHashMap<>(10);

        map.put(ContractSigningPreviewViewModel.class, ContractSigningPreviewViewModelFactory.create(contractModule, getDocumentsFromVerificationBankUseCaseProvider, fetchPdfUseCaseProvider, getIdentificationUseCaseProvider, fetchingAuthorizedIBanStatusUseCaseProvider));
        map.put(IdentityActivityViewModel.class, IdentityActivityViewModelFactory.create(identityModule, getIdentificationUseCaseProvider, identificationStepPreferencesProvider));
        map.put(VerificationPhoneViewModel.class, VerificationPhoneViewModelFactory.create(identityModule, authorizeVerificationPhoneUseCaseProvider, confirmVerificationPhoneUseCaseProvider));
        map.put(VerificationPhoneSuccessViewModel.class, VerificationPhoneSuccessViewModelFactory.create(identityModule));
        map.put(VerificationPhoneErrorViewModel.class, VerificationPhoneErrorViewModelFactory.create(identityModule));
        map.put(VerificationBankIbanViewModel.class, VerificationBankIbanViewModelFactory.create(verificationBankModule, verifyIBanUseCaseProvider));
        map.put(ProcessingVerificationViewModel.class, ProcessingVerificationViewModelFactory.create(verificationBankModule, processingVerificationUseCaseProvider));

        return map;
    }
}
