package de.solarisbank.identhub.session.data.di

import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions

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