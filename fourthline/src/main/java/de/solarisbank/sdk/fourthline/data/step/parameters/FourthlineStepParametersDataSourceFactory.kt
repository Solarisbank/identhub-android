package de.solarisbank.sdk.fourthline.data.step.parameters

import de.solarisbank.sdk.feature.di.internal.Factory

class FourthlineStepParametersDataSourceFactory private constructor(

) : Factory<FourthlineStepParametersDataSource>{

    override fun get(): FourthlineStepParametersDataSource {
        return FourthlineStepParametersDataSource()
    }

    companion object {
        fun create(): FourthlineStepParametersDataSourceFactory {
            return FourthlineStepParametersDataSourceFactory()
        }
    }
}