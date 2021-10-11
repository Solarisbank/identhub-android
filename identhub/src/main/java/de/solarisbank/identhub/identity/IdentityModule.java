package de.solarisbank.identhub.identity;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;

import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel;
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel;
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase;
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase;
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase;
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.identhub.domain.contract.GetMobileNumberUseCase;
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase;
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCase;
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModel;
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel;
import de.solarisbank.identhub.verfication.phone.PhoneVerificationUseCase;
import de.solarisbank.identhub.verfication.phone.PhoneVerificationViewModel;
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModel;
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase;

public final class IdentityModule {

    @NonNull
    public IdentityActivityViewModel provideIdentityActivityViewModel(
            final GetIdentificationUseCase getIdentificationUseCase,
            final IdentificationStepPreferences identificationStepPreferences) {
        return new IdentityActivityViewModel(getIdentificationUseCase, identificationStepPreferences);
    }

    @NonNull
    public PhoneVerificationViewModel providePhoneVerificationViewModel(final PhoneVerificationUseCase phoneVerificationUseCase) {
        return new PhoneVerificationViewModel(phoneVerificationUseCase);
    }

    @NonNull
    public VerificationPhoneSuccessViewModel provideVerificationPhoneSuccessViewModel() {
        return new VerificationPhoneSuccessViewModel();
    }

    @NonNull
    public ProcessingVerificationViewModel provideProcessingVerificationViewModel(ProcessingVerificationUseCase processingVerificationUseCase) {
        return new ProcessingVerificationViewModel(processingVerificationUseCase);
    }

    @NonNull
    public ContractSigningViewModel provideContractSigningViewModel(
            final AuthorizeContractSignUseCase authorizeContractSignUseCase,
            final ConfirmContractSignUseCase confirmContractSignUseCase,
            final IdentificationPollingStatusUseCase identificationPollingStatusUseCase,
            final GetMobileNumberUseCase getMobileNumberUseCase
    ) {
        return new ContractSigningViewModel(
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
            final GetIdentificationUseCase getIdentificationUseCase,
            final FetchingAuthorizedIBanStatusUseCase fetchingAuthorizedIBanStatusUseCase
    ) {
        return new ContractSigningPreviewViewModel(getDocumentsUseCase, fetchPdfUseCase, getIdentificationUseCase, fetchingAuthorizedIBanStatusUseCase);
    }

    @NonNull
    public VerificationBankExternalGateViewModel provideVerificationBankExternalGateViewModel(final SavedStateHandle savedStateHandle,
                                                                                              final FetchingAuthorizedIBanStatusUseCase fetchingAuthorizedIBanStatusUseCase,
                                                                                              final GetIdentificationUseCase getIdentificationUseCase) {
        return new VerificationBankExternalGateViewModel(savedStateHandle, fetchingAuthorizedIBanStatusUseCase, getIdentificationUseCase);
    }
}
