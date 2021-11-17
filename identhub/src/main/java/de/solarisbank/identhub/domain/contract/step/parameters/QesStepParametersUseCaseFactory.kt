package de.solarisbank.identhub.domain.contract.step.parameters

import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepository
import de.solarisbank.sdk.feature.di.internal.Factory

class QesStepParametersUseCaseFactory private constructor(
    private val qesStepParametersRepository: QesStepParametersRepository
) : Factory<QesStepParametersUseCase> {

    override fun get(): QesStepParametersUseCase {
        return QesStepParametersUseCase(qesStepParametersRepository)
    }

    companion object {
        fun create(
            qesStepParametersRepository: QesStepParametersRepository
        ): QesStepParametersUseCaseFactory {
            return QesStepParametersUseCaseFactory(qesStepParametersRepository)
        }
    }

}