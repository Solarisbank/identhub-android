package de.solarisbank.sdk.fourthline.data.identification.factory

import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationApi
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationModule
import retrofit2.Retrofit

class ProvideFourthlineIdentificationApiFactory private constructor(
        private val fourthlineIdentificationModule: FourthlineIdentificationModule,
        private val retrofitProvider: Provider<Retrofit>
) : Factory<FourthlineIdentificationApi> {

    override fun get(): FourthlineIdentificationApi {
        return fourthlineIdentificationModule.provideFourthlineIdentificationApi(retrofitProvider.get())
    }

    companion object {
        @JvmStatic
        fun create(
                fourthlineIdentificationModule: FourthlineIdentificationModule,
                retrofitProvider: Provider<Retrofit>
        ): ProvideFourthlineIdentificationApiFactory {
            return ProvideFourthlineIdentificationApiFactory(
                    fourthlineIdentificationModule,
                    retrofitProvider
            )
        }
    }
}