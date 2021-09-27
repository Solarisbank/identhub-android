package de.solarisbank.identhub.domain.contract

import de.solarisbank.sdk.data.entity.Identification
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.domain.NextStepSelector
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single

class GetIdentificationUseCase(
    private val contractSignRepository: ContractSignRepository,
    override val identityInitializationRepository: IdentityInitializationRepository
) : SingleUseCase<Unit, Identification>(), NextStepSelector {

    override fun invoke(param: Unit): Single<NavigationalResult<Identification>> {
        return contractSignRepository.getIdentification()
            .map {
                val nextStep = selectNextStep(it.nextStep, it.fallbackStep)
                NavigationalResult(it, nextStep)
            }

    }
}