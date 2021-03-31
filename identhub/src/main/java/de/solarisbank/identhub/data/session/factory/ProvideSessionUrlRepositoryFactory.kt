package de.solarisbank.identhub.data.session.factory

import de.solarisbank.identhub.data.session.SessionModule
import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider

class ProvideSessionUrlRepositoryFactory(
        private val sessionModule: SessionModule,
        private val sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
) : Factory<SessionUrlRepository> {
    override fun get(): SessionUrlRepository {
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