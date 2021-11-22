package de.solarisbank.identhub.identity

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepository
import de.solarisbank.identhub.domain.contract.*
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCase
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModel
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel
import de.solarisbank.identhub.verfication.phone.PhoneVerificationUseCase
import de.solarisbank.identhub.verfication.phone.PhoneVerificationViewModel
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModel
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase

class IdentityModule {
    fun provideIdentityActivityViewModel(
        getIdentificationUseCase: GetIdentificationUseCase?,
        identificationStepPreferences: IdentificationStepPreferences?
    ): IdentityActivityViewModel {
        return IdentityActivityViewModel(
            getIdentificationUseCase,
            identificationStepPreferences
        )
    }

    fun providePhoneVerificationViewModel(
        phoneVerificationUseCase: PhoneVerificationUseCase
    ): PhoneVerificationViewModel {
        return PhoneVerificationViewModel(phoneVerificationUseCase)
    }

    fun provideVerificationPhoneSuccessViewModel(): VerificationPhoneSuccessViewModel {
        return VerificationPhoneSuccessViewModel()
    }

    fun provideProcessingVerificationViewModel(
        processingVerificationUseCase: ProcessingVerificationUseCase
    ): ProcessingVerificationViewModel {
        return ProcessingVerificationViewModel(processingVerificationUseCase)
    }

    fun provideContractSigningViewModel(
        authorizeContractSignUseCase: AuthorizeContractSignUseCase,
        confirmContractSignUseCase: ConfirmContractSignUseCase,
        identificationPollingStatusUseCase: IdentificationPollingStatusUseCase,
        getMobileNumberUseCase: GetMobileNumberUseCase,
        qesStepParametersRepository: QesStepParametersRepository
    ): ContractSigningViewModel {
        return ContractSigningViewModel(
            authorizeContractSignUseCase,
            confirmContractSignUseCase,
            getMobileNumberUseCase
        )
    }

    fun provideContractSigningPreviewViewModel(
        getDocumentsUseCase: GetDocumentsUseCase,
        fetchPdfUseCase: FetchPdfUseCase,
        getIdentificationUseCase: GetIdentificationUseCase,
        fetchingAuthorizedIBanStatusUseCase: FetchingAuthorizedIBanStatusUseCase
    ): ContractSigningPreviewViewModel {
        return ContractSigningPreviewViewModel(
            getDocumentsUseCase,
            fetchPdfUseCase,
            getIdentificationUseCase,
            fetchingAuthorizedIBanStatusUseCase
        )
    }

    fun provideVerificationBankExternalGateViewModel(
        savedStateHandle: SavedStateHandle,
        fetchingAuthorizedIBanStatusUseCase: FetchingAuthorizedIBanStatusUseCase,
        getIdentificationUseCase: GetIdentificationUseCase
    ): VerificationBankExternalGateViewModel {
        return VerificationBankExternalGateViewModel(
            savedStateHandle,
            fetchingAuthorizedIBanStatusUseCase,
            getIdentificationUseCase
        )
    }

}