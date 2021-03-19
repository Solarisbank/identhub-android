package de.solarisbank.identhub.data.session.factory

import de.solarisbank.identhub.data.session.SessionModule
import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource
import de.solarisbank.identhub.di.internal.Factory
import de.solarisbank.identhub.di.internal.Preconditions

class SessionUrlLocalDataSourceFactory(
        private val sessionModule: SessionModule
) : Factory<SessionUrlLocalDataSource> {
    override fun get(): SessionUrlLocalDataSource {
        return Preconditions.checkNotNull(
                sessionModule.provideSessionUrlLocalDataSource(),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(sessionModule: SessionModule): SessionUrlLocalDataSourceFactory {
            return SessionUrlLocalDataSourceFactory(sessionModule)
        }
    }
}