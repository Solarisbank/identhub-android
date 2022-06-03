package de.solarisbank.sdk.feature.config

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class InitializationInfoRepositoryFactory (
    private val coreModule: CoreModule,
    private val savedStateHandle: SavedStateHandle,
    private val initializationInfoRetrofitDataSourceProvider: Provider<InitializationInfoRetrofitDataSource>,
    private val sessionUrlRepositoryProvider: Provider<SessionUrlRepository>
) : Factory<InitializationInfoRepository> {

    override fun get(): InitializationInfoRepository {
        return coreModule.provideInitializationInfoRepository(
            savedStateHandle,
            initializationInfoRetrofitDataSourceProvider.get(),
            sessionUrlRepositoryProvider.get()
        )
    }
}