package de.solarisbank.identhub.contract

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.domain.contract.*
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase

class ContractModule {
    fun provideContractViewModel(
            savedStateHandle: SavedStateHandle,
            identificationStepPreferences: IdentificationStepPreferences,
            sessionUrlRepository: SessionUrlRepository
    ): ContractViewModel {
        return ContractViewModel(
                savedStateHandle,
                identificationStepPreferences,
                sessionUrlRepository
        )
    }

    fun provideContractSigningPreviewViewModel(
            getDocumentsUseCase: GetDocumentsUseCase,
            fetchPdfUseCase: FetchPdfUseCase,
            getIdentificationUseCase: GetIdentificationUseCase
    ): ContractSigningPreviewViewModel {
        return ContractSigningPreviewViewModel(getDocumentsUseCase, fetchPdfUseCase, getIdentificationUseCase)
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