package de.solarisbank.identhub.contract.sign

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.contract.ContractModule
import de.solarisbank.identhub.domain.contract.AuthorizeContractSignUseCase
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase
import de.solarisbank.identhub.domain.contract.GetMobileNumberUseCase
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class ContractSigningViewModelFactory(
    private val contractModule: ContractModule,
    private val authorizeContractSignUseCaseProvider: Provider<AuthorizeContractSignUseCase>,
    private val confirmContractSignUseCaseProvider: Provider<ConfirmContractSignUseCase>,
    private val identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>,
    private val getMobileNumberUseCaseProvider: Provider<GetMobileNumberUseCase>
) : Factory<ViewModel> {

    override fun get(): ViewModel {
        return contractModule.provideContractSigningViewModel(
            authorizeContractSignUseCaseProvider.get(),
            confirmContractSignUseCaseProvider.get(),
            identificationPollingStatusUseCaseProvider.get(),
            getMobileNumberUseCaseProvider.get()
        )
    }
}