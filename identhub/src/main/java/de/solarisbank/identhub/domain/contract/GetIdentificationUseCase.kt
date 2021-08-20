package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.data.entity.NavigationalResult
import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.identhub.domain.session.NextStepSelector
import de.solarisbank.identhub.domain.usecase.SingleUseCase
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