package de.solarisbank.identhub.domain.session

import android.content.SharedPreferences
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider

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