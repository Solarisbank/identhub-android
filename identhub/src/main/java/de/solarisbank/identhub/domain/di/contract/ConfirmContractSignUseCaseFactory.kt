package de.solarisbank.identhub.domain.di.contract

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepository
import de.solarisbank.identhub.domain.contract.ConfirmContractSignUseCase
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

class ConfirmContractSignUseCaseFactory(
    private val contractSignRepositoryProvider: Provider<ContractSignRepository>,
    private val identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>,
    private val qesStepParametersRepositoryProvider: Provider<QesStepParametersRepository>
) : Factory<ConfirmContractSignUseCase> {
    override fun get(): ConfirmContractSignUseCase {
        return Preconditions.checkNotNull(
            ConfirmContractSignUseCase(
                contractSignRepositoryProvider.get(),
                identificationPollingStatusUseCaseProvider.get(),
                qesStepParametersRepositoryProvider.get()
            ),
            "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            contractSignRepositoryProvider: Provider<ContractSignRepository>,
            identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>,
            qesStepParametersRepositoryProvider: Provider<QesStepParametersRepository>
        ): ConfirmContractSignUseCaseFactory {
            return ConfirmContractSignUseCaseFactory(
                contractSignRepositoryProvider,
                identificationPollingStatusUseCaseProvider,
                qesStepParametersRepositoryProvider
            )
        }
    }
}