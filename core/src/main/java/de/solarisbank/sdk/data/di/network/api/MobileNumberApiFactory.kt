package de.solarisbank.sdk.data.di.network.api

import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.feature.di.internal.Factory
import retrofit2.Retrofit

class MobileNumberApiFactory private constructor(
        private val retrofit: Retrofit
): Factory<MobileNumberApi> {

    override fun get(): MobileNumberApi {
        return retrofit.create(MobileNumberApi::class.java)
    }

    companion object {
        @JvmStatic
        fun create(retrofit: Retrofit): MobileNumberApiFactory {
            return MobileNumberApiFactory(retrofit)
        }
    }

}