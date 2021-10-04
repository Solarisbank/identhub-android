package de.solarisbank.sdk.feature.config

import android.content.SharedPreferences
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class InitializationInfoRepositoryFactory (
    private val coreModule: CoreModule,
    private val initializationInfoRetrofitDataSourceProvider: Provider<InitializationInfoRetrofitDataSource>,
    private val sessionUrlRepositoryProvider: Provider<SessionUrlRepository>
) : Factory<InitializationInfoRepository> {

    override fun get(): InitializationInfoRepository {
        return coreModule.provideInitializationInfoRepository(
            initializationInfoRetrofitDataSourceProvider.get(),
            sessionUrlRepositoryProvider.get()
        )
    }
}