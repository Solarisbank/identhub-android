package de.solarisbank.identhub.session.data.mobile.number

import de.solarisbank.sdk.core.di.internal.Factory
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