package de.solarisbank.identhub.contract

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase
import de.solarisbank.identhub.domain.contract.GetDocumentsUseCase
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase

class ContractModule {
    fun provideContractViewModel(savedStateHandle: SavedStateHandle, identificationStepPreferences: IdentificationStepPreferences, sessionUrlRepository: SessionUrlRepository): ContractViewModel {
        return ContractViewModel(savedStateHandle, identificationStepPreferences, sessionUrlRepository)
    }

    fun provideContractSigningPreviewViewModel(getDocumentsUseCase: GetDocumentsUseCase, fetchPdfUseCase: FetchPdfUseCase): ContractSigningPreviewViewModel {
        return ContractSigningPreviewViewModel(getDocumentsUseCase, fetchPdfUseCase)
    }


    fun provideContractSigningViewModel(
            savedStateHandle: SavedStateHandle,
            authorizeContractSignUseCase: AuthorizeContractSignUseCase,
            confirmContractSignUseCase: ConfirmContractSignUseCase,
            identificationPollingStatusUseCase: IdentificationPollingStatusUseCase): ContractSigningViewModel {
        return ContractSigningViewModel(savedStateHandle, authorizeContractSignUseCase, confirmContractSignUseCase, identificationPollingStatusUseCase)
    }
}