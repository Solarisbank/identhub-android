package de.solarisbank.identhub.data.preferences

import android.content.SharedPreferences
import de.solarisbank.identhub.di.ActivityModule
import de.solarisbank.identhub.di.internal.Factory
import de.solarisbank.identhub.di.internal.Preconditions
import de.solarisbank.identhub.di.internal.Provider

class IdentificationStepPreferencesFactory(
        private val activityModule: ActivityModule,
        private val sharedPreferencesProvider: Provider<SharedPreferences>
) : Factory<IdentificationStepPreferences> {
    override fun get(): IdentificationStepPreferences {
        return Preconditions.checkNotNull(
                activityModule.provideIdentificationStepPreferences(sharedPreferencesProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
                activityModule: ActivityModule,
                sharedPreferencesProvider: Provider<SharedPreferences>
        ): IdentificationStepPreferencesFactory {
            return IdentificationStepPreferencesFactory(activityModule, sharedPreferencesProvider)
        }
    }
}