package de.solarisbank.sdk.fourthline.domain.step.parameters

import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.fourthline.data.step.parameters.FourthlineStepParametersRepository

class FourthlineStepParametersUseCaseFactory private constructor(
    private val fourthlineStepParametersRepository: FourthlineStepParametersRepository
) : Factory<FourthlineStepParametersUseCase> {

    override fun get(): FourthlineStepParametersUseCase {
        return FourthlineStepParametersUseCase(fourthlineStepParametersRepository)
    }

    companion object {
        fun create(
            fourthlineStepParametersRepository: FourthlineStepParametersRepository
        ): FourthlineStepParametersUseCaseFactory {
            return FourthlineStepParametersUseCaseFactory(fourthlineStepParametersRepository)
        }
    }

}