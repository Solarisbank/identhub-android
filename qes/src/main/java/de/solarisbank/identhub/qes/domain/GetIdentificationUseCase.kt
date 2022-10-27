package de.solarisbank.identhub.qes.domain

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.initial.IdenthubInitialConfig
import de.solarisbank.sdk.data.initial.InitialConfigStorage
import de.solarisbank.sdk.domain.NextStepSelector
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single

class GetIdentificationUseCase(
    private val contractSignRepository: ContractSignRepository,
    override val initialConfigStorage: InitialConfigStorage
) : SingleUseCase<Unit, IdentificationDto>(), NextStepSelector {

    fun getInitialConfig(): IdenthubInitialConfig {
        return initialConfigStorage.get()
    }

    override fun invoke(param: Unit): Single<NavigationalResult<IdentificationDto>> {
        return contractSignRepository.getIdentification()
            .map {
                val nextStep = selectNextStep(it.nextStep, it.fallbackStep)
                NavigationalResult(it, nextStep)
            }

    }
}