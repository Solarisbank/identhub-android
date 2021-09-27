package de.solarisbank.identhub.session.data.di

import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

class ProvideSessionUrlRepositoryFactory(
    private val sessionModule: SessionModule,
    private val sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
) : Factory<de.solarisbank.sdk.data.repository.SessionUrlRepository> {
    override fun get(): de.solarisbank.sdk.data.repository.SessionUrlRepository {
        return Preconditions.checkNotNull(
                sessionModule.provideSessionUrlRepository(sessionUrlLocalDataSourceProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            sessionModule: SessionModule,
            sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
        ): ProvideSessionUrlRepositoryFactory {
            return ProvideSessionUrlRepositoryFactory(sessionModule, sessionUrlLocalDataSourceProvider)
        }
    }
}