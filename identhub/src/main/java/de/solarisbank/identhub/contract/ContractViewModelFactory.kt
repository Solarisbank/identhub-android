package de.solarisbank.identhub.contract

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.domain.contract.step.parameters.QesStepParametersUseCase
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider

class ContractViewModelFactory(
    private val contractUiModule: ContractUiModule,
    private val sessionUrlRepositoryProvider: Provider<SessionUrlRepository>,
    private val getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>,
    private val qesStepParametersUseCaseProvider: Provider<QesStepParametersUseCase>
    ) : Factory2<ViewModel, SavedStateHandle> {

    override fun create(savedStateHandle: SavedStateHandle): ViewModel {
        return contractUiModule.provideContractViewModel(
            savedStateHandle,
            sessionUrlRepositoryProvider.get(),
            getIdentificationUseCaseProvider.get(),
            qesStepParametersUseCaseProvider.get()
        )
    }

    companion object {
        @JvmStatic
        fun create(
            contractUiModule: ContractUiModule,
            sessionUrlRepositoryProvider: Provider<SessionUrlRepository>,
            getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>,
            qesStepParametersUseCaseProvider: Provider<QesStepParametersUseCase>
        ): Factory2<ViewModel, SavedStateHandle> {
            return ContractViewModelFactory(
                contractUiModule,
                sessionUrlRepositoryProvider,
                getIdentificationUseCaseProvider,
                qesStepParametersUseCaseProvider
            )
        }
    }
}