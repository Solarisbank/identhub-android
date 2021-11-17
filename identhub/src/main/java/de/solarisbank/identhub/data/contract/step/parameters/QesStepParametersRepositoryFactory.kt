package de.solarisbank.identhub.data.contract.step.parameters

import de.solarisbank.sdk.feature.di.internal.Factory

class QesStepParametersRepositoryFactory private constructor(
    val qesStepParametersDataSource: QesStepParametersDataSource
) : Factory<QesStepParametersRepository> {

    override fun get(): QesStepParametersRepository {
        return QesStepParametersRepository(qesStepParametersDataSource)
    }

    companion object {
        fun create(
            qesStepParametersDataSource: QesStepParametersDataSource
        ) : QesStepParametersRepositoryFactory {
            return QesStepParametersRepositoryFactory(qesStepParametersDataSource)
        }
    }

}