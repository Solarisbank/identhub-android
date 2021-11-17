package de.solarisbank.identhub.data.contract.step.parameters

import de.solarisbank.sdk.feature.di.internal.Factory

class QesStepParametersDataSourceFactory private constructor(

) : Factory<QesStepParametersDataSource>{

    override fun get(): QesStepParametersDataSource {
        return QesStepParametersDataSource()
    }

    companion object {
        fun create(): QesStepParametersDataSourceFactory {
            return QesStepParametersDataSourceFactory()
        }
    }
}