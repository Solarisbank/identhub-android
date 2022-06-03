package de.solarisbank.identhub.session.data.di

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.session.data.datasource.SessionStateSavedStateHandleDataSource
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions

class ProvideSessionStateDataSourceFactory private constructor(
    private val sessionStateHandle: SavedStateHandle
) : Factory<SessionStateSavedStateHandleDataSource> {

    override fun get(): SessionStateSavedStateHandleDataSource {
        return Preconditions.checkNotNull(
            SessionStateSavedStateHandleDataSource(sessionStateHandle)
        )
    }

    companion object {
        @JvmStatic
        fun create(
            sessionStateHandle: SavedStateHandle
        ): ProvideSessionStateDataSourceFactory {
            return ProvideSessionStateDataSourceFactory(sessionStateHandle)
        }
    }
}