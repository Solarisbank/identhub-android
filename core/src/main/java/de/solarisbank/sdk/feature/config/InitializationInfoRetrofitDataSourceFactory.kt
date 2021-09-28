package de.solarisbank.sdk.feature.config

import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider


class InitializationInfoRetrofitDataSourceFactory (
    private val coreModule: CoreModule,
    private val apiProvider: Provider<InitializationInfoApi>
) : Factory<InitializationInfoRetrofitDataSource> {

    override fun get(): InitializationInfoRetrofitDataSource {
        return coreModule.provideInitializationInfoRetrofitDataSource(apiProvider.get())
    }
}