package de.solarisbank.identhub.contract

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.contract.preview.ContractSigningPreviewViewModel
import de.solarisbank.identhub.contract.sign.ContractSigningViewModel
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepository
import de.solarisbank.identhub.domain.contract.*
import de.solarisbank.identhub.domain.contract.step.parameters.QesStepParametersUseCase
import de.solarisbank.identhub.domain.verification.bank.FetchingAuthorizedIBanStatusUseCase
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase

class ContractUiModule {
    fun provideContractViewModel(
        savedStateHandle: SavedStateHandle,
        identificationStepPreferences: IdentificationStepPreferences,
        sessionUrlRepository: SessionUrlRepository,
        getIdentificationUseCase: GetIdentificationUseCase,
        qesStepParametersUseCase: QesStepParametersUseCase
    ): ContractViewModel {
        return ContractViewModel(
                savedStateHandle,
                identificationStepPreferences,
                sessionUrlRepository,
                getIdentificationUseCase,
            qesStepParametersUseCase
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
            identificationPollingStatusUseCase,
            getMobileNumberUseCase,
            qesStepParametersRepository
        )
    }
}