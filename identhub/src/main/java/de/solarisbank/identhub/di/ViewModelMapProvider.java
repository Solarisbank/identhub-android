package de.solarisbank.identhub.di;

import androidx.annotation.RestrictTo;
import androidx.lifecycle.ViewModel;

import java.util.LinkedHashMap;
import java.util.Map;

import de.solarisbank.identhub.contract.ContractUiModule;
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel;
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModelFactory;
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel;
import de.solarisbank.identhub.contract.sign.ContractSigningViewModelFactory;
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase;
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.contract.GetMobileNumberUseCase;
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCase;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCase;
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase;
import de.solarisbank.identhub.domain.verification.phone.AuthorizeVerificationPhoneUseCase;
import de.solarisbank.identhub.domain.verification.phone.ConfirmVerificationPhoneUseCase;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;
import de.solarisbank.identhub.identity.IdentityActivityViewModelFactory;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModel;
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModelFactory;
import de.solarisbank.identhub.verfication.bank.VerificationBankModule;
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel;
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModelFactory;
import de.solarisbank.identhub.verfication.phone.PhoneVerificationUseCase;
import de.solarisbank.identhub.verfication.phone.PhoneVerificationUseCaseImpl;
import de.solarisbank.identhub.verfication.phone.PhoneVerificationViewModel;
import de.solarisbank.identhub.verfication.phone.PhoneVerificationViewModelFactory;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModel;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModelFactory;
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase;
import de.solarisbank.sdk.feature.alert.AlertViewModel;
import de.solarisbank.sdk.feature.alert.AlertViewModelFactory;
import de.solarisbank.sdk.feature.config.InitializationInfoRepository;
import de.solarisbank.sdk.feature.customization.CustomizationRepository;
import de.solarisbank.sdk.feature.di.CoreModule;
import de.solarisbank.sdk.feature.di.internal.Provider;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ViewModelMapProvider implements Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> {

    private final CoreModule coreModule;
    private final IdentityModule identityModule;
    private final VerificationBankModule verificationBankModule;
    private final ContractUiModule contractUiModule;

    private final Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider;
    private final Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider;
    private final Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;
    private final Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider;
    private final Provider<FetchPdfUseCase> fetchPdfUseCaseProvider;
    private final Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider;
    private final Provider<IdentificationStepPreferences> identificationStepPreferencesProvider;
    private final Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider;
    private final Provider<BankIdPostUseCase> bankIdPostUseCaseProvider;
    private final Provider<ProcessingVerificationUseCase> processingVerificationUseCaseProvider;
    private final Provider<CustomizationRepository> customizationRepositoryProvider;
    private final Provider<InitializationInfoRepository> initializationInfoRepositoryProvider;
    private final Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider;
    private final Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider;
    private final Provider<GetMobileNumberUseCase> getMobileNumberUseCaseProvider;

    public ViewModelMapProvider(
            CoreModule coreModule,
            IdentityModule identityModule,
            VerificationBankModule verificationBankModule,
            ContractUiModule contractUiModule,
            Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider,
            Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider,
            Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider,
            Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
            Provider<FetchingAuthorizedIBanStatusUseCase> fetchingAuthorizedIBanStatusUseCaseProvider,
            Provider<FetchPdfUseCase> fetchPdfUseCaseProvider,
            Provider<IdentificationStepPreferences> identificationStepPreferencesProvider,
            Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider,
            Provider<IdentificationPollingStatusUseCase> identificationPollingStatusUseCaseProvider,
            Provider<BankIdPostUseCase> bankIdPostUseCaseProvider,
            Provider<ProcessingVerificationUseCase> processingVerificationUseCaseProvider,
            Provider<CustomizationRepository> customizationRepositoryProvider,
            Provider<InitializationInfoRepository> initializationInfoRepositoryProvider,
            Provider<AuthorizeContractSignUseCase> authorizeContractSignUseCaseProvider,
            Provider<ConfirmContractSignUseCase> confirmContractSignUseCaseProvider,
            Provider<GetMobileNumberUseCase> getMobileNumberUseCaseProvider
    ) {
        this.coreModule = coreModule;
        this.identityModule = identityModule;
        this.verificationBankModule = verificationBankModule;
        this.contractUiModule = contractUiModule;
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
        this.customizationRepositoryProvider = customizationRepositoryProvider;
        this.initializationInfoRepositoryProvider = initializationInfoRepositoryProvider;
        this.authorizeContractSignUseCaseProvider = authorizeContractSignUseCaseProvider;
        this.confirmContractSignUseCaseProvider = confirmContractSignUseCaseProvider;
        this.getMobileNumberUseCaseProvider = getMobileNumberUseCaseProvider;
    }

    @Override
    public Map<Class<? extends ViewModel>, Provider<ViewModel>> get() {
        Map<Class<? extends ViewModel>, Provider<ViewModel>> map = new LinkedHashMap<>(10);

        //TODO: Inject
        Provider<PhoneVerificationUseCase> phoneVerificationUseCaseProvider = new Provider<PhoneVerificationUseCase>() {
            @Override
            public PhoneVerificationUseCase get() {
                return new PhoneVerificationUseCaseImpl(authorizeVerificationPhoneUseCaseProvider.get(), confirmVerificationPhoneUseCaseProvider.get(), getMobileNumberUseCaseProvider.get());
            }
        };

        map.put(ContractSigningPreviewViewModel.class, ContractSigningPreviewViewModelFactory.create(contractUiModule, getDocumentsFromVerificationBankUseCaseProvider, fetchPdfUseCaseProvider, getIdentificationUseCaseProvider, fetchingAuthorizedIBanStatusUseCaseProvider));
        map.put(ContractSigningViewModel.class, new ContractSigningViewModelFactory(contractUiModule, authorizeContractSignUseCaseProvider, confirmContractSignUseCaseProvider, identificationPollingStatusUseCaseProvider, getMobileNumberUseCaseProvider));
        map.put(IdentityActivityViewModel.class, IdentityActivityViewModelFactory.create(identityModule, getIdentificationUseCaseProvider, identificationStepPreferencesProvider));
        map.put(PhoneVerificationViewModel.class, new PhoneVerificationViewModelFactory(identityModule, phoneVerificationUseCaseProvider));
        map.put(VerificationPhoneSuccessViewModel.class, VerificationPhoneSuccessViewModelFactory.create(identityModule));
        map.put(VerificationBankIbanViewModel.class, new VerificationBankIbanViewModelFactory(verificationBankModule, verifyIBanUseCaseProvider, bankIdPostUseCaseProvider, initializationInfoRepositoryProvider));
        map.put(ProcessingVerificationViewModel.class, ProcessingVerificationViewModelFactory.create(verificationBankModule, processingVerificationUseCaseProvider));
        map.put(AlertViewModel.class, new AlertViewModelFactory(coreModule, customizationRepositoryProvider));

        return map;
    }
}
