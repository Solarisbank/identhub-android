package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.domain.verification.bank.BankIdPostUseCase
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCase
import de.solarisbank.identhub.domain.verification.bank.VerifyIBanUseCase
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGateViewModel
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.config.InitializationInfoRepository

class VerificationBankModule {
    fun provideVerificationBankActivityViewModel(
        savedStateHandle: SavedStateHandle,
        sessionUrlRepository: SessionUrlRepository,
        initializationInfoRepository: InitializationInfoRepository
    ): VerificationBankViewModel {
        return VerificationBankViewModel(
            savedStateHandle,
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

    fun provideVerificationBankExternalGateViewModel(
        savedStateHandle: SavedStateHandle,
        fetchingAuthorizedIBanStatusUseCase: FetchingAuthorizedIBanStatusUseCase,
        identificationLocalDataSource: IdentificationLocalDataSource
    ): VerificationBankExternalGateViewModel {
        return VerificationBankExternalGateViewModel(
            savedStateHandle,
            fetchingAuthorizedIBanStatusUseCase,
            identificationLocalDataSource
        )
    }
}