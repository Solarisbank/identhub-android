package de.solarisbank.identhub.session.data.identification

import de.solarisbank.sdk.core.di.internal.Factory

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