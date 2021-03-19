package de.solarisbank.identhub.data.session.factory

import de.solarisbank.identhub.data.session.SessionModule
import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource
import de.solarisbank.identhub.di.internal.Factory
import de.solarisbank.identhub.di.internal.Preconditions
import de.solarisbank.identhub.di.internal.Provider
import de.solarisbank.identhub.domain.session.SessionUrlRepository

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