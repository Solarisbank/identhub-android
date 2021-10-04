package de.solarisbank.sdk.feature.customization

import android.content.Context
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import de.solarisbank.sdk.feature.config.InitializationInfoRetrofitDataSource
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class CustomizationRepositoryFactory(
    private val coreModule: CoreModule,
    private val contextProvider: Provider<Context>,
    private val initializationInfoRepositoryProvider: Provider<InitializationInfoRepository>
): Factory<CustomizationRepository> {

    override fun get(): CustomizationRepository {
        return coreModule.provideCustomizationRepository(
            contextProvider.get(),
            initializationInfoRepositoryProvider.get()
        )
    }
}