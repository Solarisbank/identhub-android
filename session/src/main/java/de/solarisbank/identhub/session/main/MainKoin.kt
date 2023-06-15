package de.solarisbank.identhub.session.main

import de.solarisbank.sdk.module.abstraction.GeneralModuleLoader
import de.solarisbank.identhub.session.module.IdenthubModuleConfigurator
import de.solarisbank.identhub.session.module.IdenthubModuleResolver
import de.solarisbank.sdk.data.datasource.IdentificationInMemoryDataSource
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

object MainKoin {
    val module = module {
        single { IdenthubModuleResolver() }
        single { IdenthubModuleConfigurator() } bind GeneralModuleLoader::class
        single<IdentificationLocalDataSource> { IdentificationInMemoryDataSource() }

        viewModel { MainViewModel(get(), get(), get(), get()) }
    }
}