package de.solarisbank.identhub.session.data.ip

import de.solarisbank.sdk.feature.di.internal.Factory
import retrofit2.Retrofit

class IpApiFactory private constructor(private val retrofit: Retrofit) :
    Factory<IpApi> {

    override fun get(): IpApi {
        return retrofit.create(IpApi::class.java)
    }

    companion object {
        @JvmStatic
        fun create(retrofit: Retrofit): IpApiFactory {
            return IpApiFactory(retrofit)
        }
    }
}