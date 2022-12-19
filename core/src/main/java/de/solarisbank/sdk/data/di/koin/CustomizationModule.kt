package de.solarisbank.sdk.data.di.koin

import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.data.customization.CustomizationRepositoryImpl
import org.koin.dsl.module

internal val customizationModule = module {
    single<CustomizationRepository> { CustomizationRepositoryImpl(get(), getOrNull()) }
}