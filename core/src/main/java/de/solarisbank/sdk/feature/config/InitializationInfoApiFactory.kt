package de.solarisbank.sdk.feature.config

import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider
import retrofit2.Retrofit


class InitializationInfoApiFactory (
    private val coreModule: CoreModule,
    private val retrofitProvider: Provider<Retrofit>
) : Factory<InitializationInfoApi> {

    override fun get(): InitializationInfoApi {
        return coreModule.provideInitializationInfoApi(retrofitProvider.get())
    }
}