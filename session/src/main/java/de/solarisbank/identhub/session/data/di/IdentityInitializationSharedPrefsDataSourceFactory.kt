package de.solarisbank.identhub.session.data.di

import android.content.SharedPreferences
import de.solarisbank.identhub.session.data.datasource.IdentityInitializationSharedPrefsDataSource
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

class IdentityInitializationSharedPrefsDataSourceFactory private constructor(
    private val sharedPreferenceProvider: Provider<SharedPreferences>
) : Factory<IdentityInitializationSharedPrefsDataSource> {
    override fun get(): IdentityInitializationSharedPrefsDataSource {
        return Preconditions.checkNotNull(
            IdentityInitializationSharedPrefsDataSource(sharedPreferenceProvider.get()),
            "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            sharedPreferenceProvider: Provider<SharedPreferences>
        ): IdentityInitializationSharedPrefsDataSourceFactory {
            return IdentityInitializationSharedPrefsDataSourceFactory(sharedPreferenceProvider)
        }
    }
}