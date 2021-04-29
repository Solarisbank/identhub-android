package de.solarisbank.identhub.identity;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;

import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel;
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel;
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase;
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase;
import de.solarisbank.identhub.domain.contract.DeleteAllLocalStorageUseCase;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase;
import de.solarisbank.identhub.domain.verification.phone.AuthorizeVerificationPhoneUseCase;
import de.solarisbank.identhub.domain.verification.phone.ConfirmVerificationPhoneUseCase;
import de.solarisbank.identhub.identity.summary.IdentitySummaryFragmentViewModel;
import de.solarisbank.identhub.identity.summary.IdentitySummaryViewModel;
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModel;
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorViewModel;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModel;
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel;
import de.solarisbank.identhub.verfication.phone.VerificationPhoneViewModel;
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorViewModel;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModel;

public final class IdentityModule {

    @NonNull
    public IdentityActivityViewModel provideIdentityActivityViewModel(
            final GetIdentificationUseCase getIdentificationUseCase,
            final IdentificationStepPreferences identificationStepPreferences) {
        return new IdentityActivityViewModel(getIdentificationUseCase, identificationStepPreferences);
    }

    @NonNull
    public VerificationPhoneViewModel provideVerificationPhoneViewModel(final AuthorizeVerificationPhoneUseCase authorizeVerificationPhoneUseCase,
                                                                        final ConfirmVerificationPhoneUseCase confirmVerificationPhoneUseCase) {
        return new VerificationPhoneViewModel(authorizeVerificationPhoneUseCase, confirmVerificationPhoneUseCase);
    }

    @NonNull
    public VerificationPhoneSuccessViewModel provideVerificationPhoneSuccessViewModel() {
        return new VerificationPhoneSuccessViewModel();
    }

    @NonNull
    public VerificationPhoneErrorViewModel provideVerificationPhoneErrorViewModel() {
        return new VerificationPhoneErrorViewModel();
    }

    @NonNull
    public VerificationBankIbanViewModel provideVerificationBankViewModel(final VerifyIBanUseCase verifyIBanUseCase) {
        return new VerificationBankIbanViewModel(verifyIBanUseCase);
    }

    @NonNull
    public VerificationBankErrorViewModel provideVerificationBankErrorViewModel() {
        return new VerificationBankErrorViewModel();
    }

    @NonNull
    public ProcessingVerificationViewModel provideProcessingVerificationViewModel() {
        return new ProcessingVerificationViewModel();
    }

    @NonNull
    public ContractSigningViewModel provideContractSigningViewModel(
            final SavedStateHandle savedStateHandle,
            final AuthorizeContractSignUseCase authorizeContractSignUseCase,
            final ConfirmContractSignUseCase confirmContractSignUseCase,
            final GetIdentificationUseCase getIdentificationUseCase) {
        return new ContractSigningViewModel(savedStateHandle, authorizeContractSignUseCase, confirmContractSignUseCase, getIdentificationUseCase);
    }

    @NonNull
    public ContractSigningPreviewViewModel provideContractSigningPreviewViewModel(
            final GetDocumentsUseCase getDocumentsUseCase,
            final FetchPdfUseCase fetchPdfUseCase
    ) {
        return new ContractSigningPreviewViewModel(getDocumentsUseCase, fetchPdfUseCase);
    }

    @NonNull
    public VerificationBankExternalGateViewModel provideVerificationBankExternalGateViewModel(final SavedStateHandle savedStateHandle,
                                                                                              final FetchingAuthorizedIBanStatusUseCase fetchingAuthorizedIBanStatusUseCase,
                                                                                              final GetIdentificationUseCase getIdentificationUseCase) {
        return new VerificationBankExternalGateViewModel(savedStateHandle, fetchingAuthorizedIBanStatusUseCase, getIdentificationUseCase);
    }

    @NonNull
    public IdentitySummaryFragmentViewModel provideIdentitySummaryFragmentViewModel(
            final DeleteAllLocalStorageUseCase deleteAllLocalStorageUseCase,
            final GetDocumentsUseCase getDocumentsUseCase,
            final FetchPdfUseCase fetchPdfUseCase,
            final IdentificationStepPreferences identificationStepPreferences,
            final SavedStateHandle savedStateHandle) {
        return new IdentitySummaryFragmentViewModel(
                deleteAllLocalStorageUseCase, getDocumentsUseCase, fetchPdfUseCase, identificationStepPreferences, savedStateHandle);
    }

    @NonNull
    public IdentitySummaryViewModel provideIdentitySummaryViewModel(
            final GetIdentificationUseCase getIdentificationUseCase,
            final SavedStateHandle savedStateHandle) {
        return new IdentitySummaryViewModel(getIdentificationUseCase, savedStateHandle);
    }
}
