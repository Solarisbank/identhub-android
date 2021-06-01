package de.solarisbank.identhub.session.data.identification

import de.solarisbank.sdk.core.di.internal.Factory
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