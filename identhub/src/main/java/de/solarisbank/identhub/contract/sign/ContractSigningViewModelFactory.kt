package de.solarisbank.identhub.contract.sign

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.contract.ContractUiModule
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepository
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase
import de.solarisbank.identhub.domain.contract.GetMobileNumberUseCase
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class ContractSigningViewModelFactory(
    private val contractUiModule: ContractUiModule,
    private val authorizeContractSignUseCaseProvider: Provider<AuthorizeContractSignUseCase>,
    private val confirmContractSignUseCaseProvider: Provider<ConfirmContractSignUseCase>,
    private val identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>,
    private val getMobileNumberUseCaseProvider: Provider<GetMobileNumberUseCase>,
    private val qesStepParametersRepositoryProvider: Provider<QesStepParametersRepository>
) : Factory<ViewModel> {

    override fun get(): ViewModel {
        return contractUiModule.provideContractSigningViewModel(
            authorizeContractSignUseCaseProvider.get(),
            confirmContractSignUseCaseProvider.get(),
            identificationPollingStatusUseCaseProvider.get(),
            getMobileNumberUseCaseProvider.get(),
            qesStepParametersRepositoryProvider.get()
        )
    }
}