package de.solarisbank.sdk.data.di.koin

import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.data.di.datasource.SessionUrlMemoryDataSource
import de.solarisbank.sdk.data.repository.SessionUrlDataSourceRepository
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import org.koin.core.module.Module
import org.koin.dsl.module

internal object SessionModule {
    fun get(sessionUrl: String?): Module {
        return module {
            single<SessionUrlLocalDataSource> {
                SessionUrlMemoryDataSource().apply {
                    store(sessionUrl)
                }
            }

            single<SessionUrlRepository> {
                SessionUrlDataSourceRepository(get())
            }
        }
    }
}