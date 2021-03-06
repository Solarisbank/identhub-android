package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.domain.contract.*
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.identhub.domain.verification.bank.JointAccountBankIdPostUseCase
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.identhub.identity.summary.IdentitySummaryFragmentViewModel
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModel
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel

class VerificationBankModule {
    fun provideVerificationBankActivityViewModel(savedStateHandle: SavedStateHandle, identificationStepPreferences: IdentificationStepPreferences, getIdentificationUseCase: GetIdentificationUseCase, sessionUrlRepository: SessionUrlRepository): VerificationBankViewModel {
        return VerificationBankViewModel(savedStateHandle, identificationStepPreferences, getIdentificationUseCase, sessionUrlRepository)
    }

    fun provideVerificationBankIbanViewModel(verifyIBanUseCase: VerifyIBanUseCase): VerificationBankIbanViewModel {
        return VerificationBankIbanViewModel(verifyIBanUseCase)
    }

    fun provideProcessingVerificationViewModel(pollingStatusUseCase: IdentificationPollingStatusUseCase, bandIdPostUseCaseJointAccount: JointAccountBankIdPostUseCase): ProcessingVerificationViewModel {
        return ProcessingVerificationViewModel(pollingStatusUseCase, bandIdPostUseCaseJointAccount)
    }

    fun provideContractSigningViewModel(
            savedStateHandle: SavedStateHandle,
            authorizeContractSignUseCase: AuthorizeContractSignUseCase,
            confirmContractSignUseCase: ConfirmContractSignUseCase,
            getIdentificationUseCase: IdentificationPollingStatusUseCase): ContractSigningViewModel {
        return ContractSigningViewModel(savedStateHandle, authorizeContractSignUseCase, confirmContractSignUseCase, getIdentificationUseCase)
    }

    fun provideContractSigningPreviewViewModel(
            getDocumentsUseCase: GetDocumentsUseCase,
            fetchPdfUseCase: FetchPdfUseCase
    ): ContractSigningPreviewViewModel {
        return ContractSigningPreviewViewModel(getDocumentsUseCase, fetchPdfUseCase)
    }

    fun provideVerificationBankExternalGateViewModel(savedStateHandle: SavedStateHandle,
                                                     fetchingAuthorizedIBanStatusUseCase: FetchingAuthorizedIBanStatusUseCase,
                                                     getIdentificationUseCase: GetIdentificationUseCase): VerificationBankExternalGateViewModel {
        return VerificationBankExternalGateViewModel(savedStateHandle, fetchingAuthorizedIBanStatusUseCase, getIdentificationUseCase)
    }

    fun provideIdentitySummaryFragmentViewModel(
            deleteAllLocalStorageUseCase: DeleteAllLocalStorageUseCase,
            getDocumentsUseCase: GetDocumentsUseCase,
            fetchPdfUseCase: FetchPdfUseCase,
            identificationStepPreferences: IdentificationStepPreferences,
            getIdentificationUseCase: GetIdentificationUseCase,
            savedStateHandle: SavedStateHandle): IdentitySummaryFragmentViewModel {
        return IdentitySummaryFragmentViewModel(
                deleteAllLocalStorageUseCase, getDocumentsUseCase, fetchPdfUseCase, identificationStepPreferences, getIdentificationUseCase, savedStateHandle)
    }
}