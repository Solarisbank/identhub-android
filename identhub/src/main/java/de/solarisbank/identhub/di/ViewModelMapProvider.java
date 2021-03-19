package de.solarisbank.identhub.di;

import androidx.annotation.RestrictTo;
import androidx.lifecycle.ViewModel;

import java.util.LinkedHashMap;
import java.util.Map;

import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel;
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModelFactory;
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.di.internal.Provider;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase;
import de.solarisbank.identhub.domain.verification.phone.AuthorizeVerificationPhoneUseCase;
import de.solarisbank.identhub.domain.verification.phone.ConfirmVerificationPhoneUseCase;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;
import de.solarisbank.identhub.identity.IdentityActivityViewModelFactory;
import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModel;
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModelFactory;
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorViewModel;
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorViewModelFactory;
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel;
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModelFactory;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModel;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModelFactory;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorViewModel;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorViewModelFactory;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModel;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModelFactory;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ViewModelMapProvider implements Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> {

    private final IdentityModule identityModule;

    private final Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider;
    private final Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider;
    private final Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;
    private final Provider<FetchPdfUseCase> fetchPdfUseCaseProvider;
    private final Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider;
    private final Provider<IdentificationStepPreferences> identificationStepPreferencesProvider;

    public ViewModelMapProvider(
            IdentityModule identityModule,
            Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider,
            Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider,
            Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider,
            Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
            Provider<FetchPdfUseCase> fetchPdfUseCaseProvider,
            Provider<IdentificationStepPreferences> identificationStepPreferencesProvider,
            Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider) {
        this.identityModule = identityModule;
        this.authorizeVerificationPhoneUseCaseProvider = authorizeVerificationPhoneUseCaseProvider;
        this.confirmVerificationPhoneUseCaseProvider = confirmVerificationPhoneUseCaseProvider;
        this.getDocumentsFromVerificationBankUseCaseProvider = getDocumentsFromVerificationBankUseCaseProvider;
        this.getIdentificationUseCaseProvider = getIdentificationUseCaseProvider;
        this.fetchPdfUseCaseProvider = fetchPdfUseCaseProvider;
        this.identificationStepPreferencesProvider = identificationStepPreferencesProvider;
        this.verifyIBanUseCaseProvider = verifyIBanUseCaseProvider;
    }

    public static ViewModelMapProvider create(
            IdentityModule identityModule,
            Provider<AuthorizeVerificationPhoneUseCase> authorizeVerificationPhoneUseCaseProvider,
            Provider<ConfirmVerificationPhoneUseCase> confirmVerificationPhoneUseCaseProvider,
            Provider<GetDocumentsUseCase> getDocumentsFromVerificationBankUseCaseProvider,
            Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
            Provider<FetchPdfUseCase> fetchPdfUseCaseProvider,
            Provider<IdentificationStepPreferences> identificationStepPreferencesProvider,
            Provider<VerifyIBanUseCase> verifyIBanUseCaseProvider
    ) {
        return new ViewModelMapProvider(
                identityModule,
                authorizeVerificationPhoneUseCaseProvider,
                confirmVerificationPhoneUseCaseProvider,
                getDocumentsFromVerificationBankUseCaseProvider,
                getIdentificationUseCaseProvider,
                fetchPdfUseCaseProvider,
                identificationStepPreferencesProvider,
                verifyIBanUseCaseProvider
        );
    }

    @Override
    public Map<Class<? extends ViewModel>, Provider<ViewModel>> get() {
        Map<Class<? extends ViewModel>, Provider<ViewModel>> map = new LinkedHashMap<>(10);

        map.put(ContractSigningPreviewViewModel.class, ContractSigningPreviewViewModelFactory.create(identityModule, getDocumentsFromVerificationBankUseCaseProvider, fetchPdfUseCaseProvider));
        map.put(IdentityActivityViewModel.class, IdentityActivityViewModelFactory.create(identityModule, getIdentificationUseCaseProvider, identificationStepPreferencesProvider));
        map.put(VerificationPhoneViewModel.class, VerificationPhoneViewModelFactory.create(identityModule, authorizeVerificationPhoneUseCaseProvider, confirmVerificationPhoneUseCaseProvider));
        map.put(VerificationPhoneSuccessViewModel.class, VerificationPhoneSuccessViewModelFactory.create(identityModule));
        map.put(VerificationPhoneErrorViewModel.class, VerificationPhoneErrorViewModelFactory.create(identityModule));
        map.put(VerificationBankViewModel.class, VerificationBankViewModelFactory.create(identityModule, verifyIBanUseCaseProvider));
        map.put(VerificationBankErrorViewModel.class, VerificationBankErrorViewModelFactory.create(identityModule));
        map.put(ProcessingVerificationViewModel.class, ProcessingVerificationViewModelFactory.create(identityModule));

        return map;
    }
}
