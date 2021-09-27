package de.solarisbank.identhub.contract

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel
import de.solarisbank.identhub.domain.contract.*
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase

class ContractModule {
    fun provideContractViewModel(
        savedStateHandle: SavedStateHandle,
        identificationStepPreferences: IdentificationStepPreferences,
        sessionUrlRepository: SessionUrlRepository,
        getIdentificationUseCase: GetIdentificationUseCase
    ): ContractViewModel {
        return ContractViewModel(
                savedStateHandle,
                identificationStepPreferences,
                sessionUrlRepository,
                getIdentificationUseCase
        )
    }

    fun provideContractSigningPreviewViewModel(
            getDocumentsUseCase: GetDocumentsUseCase,
            fetchPdfUseCase: FetchPdfUseCase,
            getIdentificationUseCase: GetIdentificationUseCase,
            fetchingAuthorizedIBanStatusUseCase: FetchingAuthorizedIBanStatusUseCase
    ): ContractSigningPreviewViewModel {
        return ContractSigningPreviewViewModel(getDocumentsUseCase, fetchPdfUseCase, getIdentificationUseCase, fetchingAuthorizedIBanStatusUseCase)
    }


    fun provideContractSigningViewModel(
        savedStateHandle: SavedStateHandle,
        authorizeContractSignUseCase: AuthorizeContractSignUseCase,
        confirmContractSignUseCase: ConfirmContractSignUseCase,
        identificationPollingStatusUseCase: IdentificationPollingStatusUseCase,
        getMobileNumberUseCase: GetMobileNumberUseCase
    ): ContractSigningViewModel {
        return ContractSigningViewModel(
                savedStateHandle,
                authorizeContractSignUseCase,
                confirmContractSignUseCase,
                identificationPollingStatusUseCase,
                getMobileNumberUseCase
        )
    }
}