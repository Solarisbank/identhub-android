package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel
import de.solarisbank.identhub.domain.contract.*
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCase
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCase
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModel
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.config.InitializationInfoRepository

class VerificationBankModule {
    fun provideVerificationBankActivityViewModel(
        savedStateHandle: SavedStateHandle,
        identificationStepPreferences: IdentificationStepPreferences,
        getIdentificationUseCase: GetIdentificationUseCase,
        sessionUrlRepository: SessionUrlRepository,
        initializationInfoRepository: InitializationInfoRepository
    ): VerificationBankViewModel {
        return VerificationBankViewModel(
            savedStateHandle,
            identificationStepPreferences,
            getIdentificationUseCase,
            sessionUrlRepository,
            initializationInfoRepository
        )
    }

    fun provideVerificationBankIbanViewModel(
        verifyIBanUseCase: VerifyIBanUseCase,
        bankIdPostUseCase: BankIdPostUseCase,
        initializationInfoRepository: InitializationInfoRepository
    ): VerificationBankIbanViewModel {
        return VerificationBankIbanViewModel(verifyIBanUseCase, bankIdPostUseCase, initializationInfoRepository)
    }

    fun provideProcessingVerificationViewModel(
        processingVerificationUseCase: ProcessingVerificationUseCase
    ): ProcessingVerificationViewModel {
        return ProcessingVerificationViewModel(processingVerificationUseCase)
    }

    fun provideContractSigningViewModel(
        authorizeContractSignUseCase: AuthorizeContractSignUseCase,
        confirmContractSignUseCase: ConfirmContractSignUseCase,
        getIdentificationUseCase: IdentificationPollingStatusUseCase,
        getMobileNumberUseCase: GetMobileNumberUseCase
    ): ContractSigningViewModel {
        return ContractSigningViewModel(
                authorizeContractSignUseCase,
                confirmContractSignUseCase,
                getIdentificationUseCase,
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