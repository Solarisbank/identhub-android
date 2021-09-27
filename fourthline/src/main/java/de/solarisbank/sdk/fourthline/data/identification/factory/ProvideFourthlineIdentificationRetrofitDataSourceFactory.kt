package de.solarisbank.sdk.fourthline.data.identification.factory

import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationApi
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationModule
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRetrofitDataSource

class ProvideFourthlineIdentificationRetrofitDataSourceFactory private constructor(
        private val fourthlineIdentificationModule: FourthlineIdentificationModule,
        private val fourthlineIdentificationApiProvider: Provider<FourthlineIdentificationApi>
) : Factory<FourthlineIdentificationRetrofitDataSource> {

    override fun get(): FourthlineIdentificationRetrofitDataSource {
        return fourthlineIdentificationModule.provideFourthlineIdentificationRetrofitDataSource(
                fourthlineIdentificationApiProvider.get()
        )
    }

    companion object {
        @JvmStatic
        fun create(
                fourthlineIdentificationModule: FourthlineIdentificationModule,
                fourthlineIdentificationApiProvider: Provider<FourthlineIdentificationApi>
        ) : ProvideFourthlineIdentificationRetrofitDataSourceFactory {
            return ProvideFourthlineIdentificationRetrofitDataSourceFactory(
                    fourthlineIdentificationModule,
                    fourthlineIdentificationApiProvider
            )
        }
    }
}