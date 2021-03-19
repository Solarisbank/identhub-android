package de.solarisbank.identhub.di

import android.content.Context
import android.content.SharedPreferences
import de.solarisbank.identhub.di.internal.Factory
import de.solarisbank.identhub.di.internal.Preconditions
import de.solarisbank.identhub.di.internal.Provider

class SharedPreferencesFactory(
        private val activityModule: ActivityModule,
        private val contextProvider: Provider<Context>
) : Factory<SharedPreferences> {
    override fun get(): SharedPreferences {
        return Preconditions.checkNotNull(
                activityModule.provideSharedPreferences(contextProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
                activityModule: ActivityModule,
                contextProvider: Provider<Context>
        ): SharedPreferencesFactory {
            return SharedPreferencesFactory(activityModule, contextProvider)
        }
    }
}