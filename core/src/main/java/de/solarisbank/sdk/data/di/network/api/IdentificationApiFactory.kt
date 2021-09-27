package de.solarisbank.sdk.data.di.network.api

import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.di.IdentificationModule
import de.solarisbank.sdk.feature.di.internal.Factory
import retrofit2.Retrofit

class IdentificationApiFactory private constructor(
    private val identificationModule: IdentificationModule,
    private val retrofit: Retrofit
        ) : Factory<IdentificationApi> {



    override fun get(): IdentificationApi {
        return identificationModule.provideIdentificationStatusApi(retrofit)
    }

    companion object {
        @JvmStatic
        fun create(identificationModule: IdentificationModule, retrofit: Retrofit): IdentificationApiFactory {
            return IdentificationApiFactory(identificationModule, retrofit)
        }
    }

}