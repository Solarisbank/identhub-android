package de.solarisbank.sdk.fourthline.data.step.parameters

import de.solarisbank.sdk.feature.di.internal.Factory

class FourthlineStepParametersRepositoryFactory private constructor(
    val fourthlineStepParametersDataSource: FourthlineStepParametersDataSource
) : Factory<FourthlineStepParametersRepository> {

    override fun get(): FourthlineStepParametersRepository {
        return FourthlineStepParametersRepository(fourthlineStepParametersDataSource)
    }

    companion object {
        fun create(
            fourthlineStepParametersDataSource: FourthlineStepParametersDataSource
        ) : FourthlineStepParametersRepositoryFactory {
            return FourthlineStepParametersRepositoryFactory(fourthlineStepParametersDataSource)
        }
    }

}