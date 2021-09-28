package de.solarisbank.sdk.feature.customization

import android.content.Context
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSource
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class CustomizationRepositoryFactory(
    private val coreModule: CoreModule,
    private val contextProvider: Provider<Context>,
    private val initializationInfoRetrofitDataSourceProvider: Provider<InitializationInfoRetrofitDataSource>,
    private val customizationSharedPrefsStoreProvider: Provider<CustomizationSharedPrefsStore>,
    private val sessionUrlRepositoryProvider: Provider<SessionUrlRepository>
): Factory<CustomizationRepository> {

    override fun get(): CustomizationRepository {
        return coreModule.provideCustomizationRepository(
            contextProvider.get(),
            initializationInfoRetrofitDataSourceProvider.get(),
            customizationSharedPrefsStoreProvider.get(),
            sessionUrlRepositoryProvider.get()
        )
    }
}