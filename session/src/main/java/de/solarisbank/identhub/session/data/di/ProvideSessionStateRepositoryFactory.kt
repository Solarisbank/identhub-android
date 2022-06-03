package de.solarisbank.identhub.session.data.di

import de.solarisbank.identhub.session.data.datasource.SessionStateSavedStateHandleDataSource
import de.solarisbank.identhub.session.data.repository.SessionStateRepository
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions

class ProvideSessionStateRepositoryFactory private constructor(
    private val sessionStateSavedStateHandleDataSource: SessionStateSavedStateHandleDataSource
) : Factory<SessionStateRepository> {

    override fun get(): SessionStateRepository {
        return Preconditions.checkNotNull(
            SessionStateRepository(sessionStateSavedStateHandleDataSource)
        )
    }

    companion object {
        fun create(
            sessionStateSavedStateHandleDataSource: SessionStateSavedStateHandleDataSource
        ): ProvideSessionStateRepositoryFactory {
           return ProvideSessionStateRepositoryFactory(sessionStateSavedStateHandleDataSource)
        }
    }
}