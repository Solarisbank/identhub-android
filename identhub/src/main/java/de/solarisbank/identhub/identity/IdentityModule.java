package de.solarisbank.identhub.identity;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;

import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel;
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel;
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase;
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.contract.GetMobileNumberUseCase;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.domain.verification.bank.JointAccountBankIdPostUseCase;
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase;
import de.solarisbank.identhub.domain.verification.phone.AuthorizeVerificationPhoneUseCase;
import de.solarisbank.identhub.domain.verification.phone.ConfirmVerificationPhoneUseCase;
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase;
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanViewModel;
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
    public ProcessingVerificationViewModel provideProcessingVerificationViewModel(IdentificationPollingStatusUseCase identificationPollingStatusUseCase, JointAccountBankIdPostUseCase jointAccountBankIdPostUseCase) {
        return new ProcessingVerificationViewModel(identificationPollingStatusUseCase, jointAccountBankIdPostUseCase);
    }

    @NonNull
    public ContractSigningViewModel provideContractSigningViewModel(
            final SavedStateHandle savedStateHandle,
            final AuthorizeContractSignUseCase authorizeContractSignUseCase,
            final ConfirmContractSignUseCase confirmContractSignUseCase,
            final IdentificationPollingStatusUseCase identificationPollingStatusUseCase,
            final GetMobileNumberUseCase getMobileNumberUseCase
    ) {
        return new ContractSigningViewModel(
                savedStateHandle,
                authorizeContractSignUseCase,
                confirmContractSignUseCase,
                identificationPollingStatusUseCase,
                getMobileNumberUseCase
        );
    }

    @NonNull
    public ContractSigningPreviewViewModel provideContractSigningPreviewViewModel(
            final GetDocumentsUseCase getDocumentsUseCase,
            final FetchPdfUseCase fetchPdfUseCase,
            final GetIdentificationUseCase getIdentificationUseCase
    ) {
        return new ContractSigningPreviewViewModel(getDocumentsUseCase, fetchPdfUseCase, getIdentificationUseCase);
    }

    @NonNull
    public VerificationBankExternalGateViewModel provideVerificationBankExternalGateViewModel(final SavedStateHandle savedStateHandle,
                                                                                              final FetchingAuthorizedIBanStatusUseCase fetchingAuthorizedIBanStatusUseCase,
                                                                                              final GetIdentificationUseCase getIdentificationUseCase) {
        return new VerificationBankExternalGateViewModel(savedStateHandle, fetchingAuthorizedIBanStatusUseCase, getIdentificationUseCase);
    }
}
