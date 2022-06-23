package de.solarisbank.sdk.logger.data

import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider
import retrofit2.Retrofit
import retrofit2.create

class LoggerApiFactory(
    private val coreModule: CoreModule,
    private val retrofitProvider: Provider<Retrofit>
) : Factory<LoggerAPI> {

    override fun get(): LoggerAPI {
        return coreModule.provideInitializeLoggerApi(retrofitProvider.get())
    }

}