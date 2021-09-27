package de.solarisbank.sdk.data.di.datasource

import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.di.IdentificationModule
import de.solarisbank.sdk.feature.di.internal.Factory

class IdentificationRetrofitDataSourceFactory private constructor(
    private val identificationModule: IdentificationModule,
    private val identificationApi: IdentificationApi
) : Factory<IdentificationRetrofitDataSource> {

    override fun get(): IdentificationRetrofitDataSource {
        return identificationModule.provideIdentificationRetorofitDataSource(identificationApi)
    }

    companion object {
        @JvmStatic
        fun create(identificationModule: IdentificationModule, identificationApi: IdentificationApi): IdentificationRetrofitDataSourceFactory {
            return IdentificationRetrofitDataSourceFactory(identificationModule, identificationApi)
        }
    }

}