package de.solarisbank.identhub.data.person

import de.solarisbank.sdk.core.di.internal.Factory
import retrofit2.Retrofit

class PersonDataApiFactory private constructor(private val retrofit: Retrofit) : Factory<PersonDataApi> {
    override fun get(): PersonDataApi {
        return retrofit.create(PersonDataApi::class.java)
    }

    companion object {
        @JvmStatic
        fun create(retrofit: Retrofit): PersonDataApiFactory {
            return PersonDataApiFactory(retrofit)
        }
    }
}