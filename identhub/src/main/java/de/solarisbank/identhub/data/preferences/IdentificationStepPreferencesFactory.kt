package de.solarisbank.identhub.data.preferences

import android.content.SharedPreferences
import de.solarisbank.identhub.di.ActivitySubModule
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider

class IdentificationStepPreferencesFactory(
        private val activitySubModule: ActivitySubModule,
        private val sharedPreferencesProvider: Provider<SharedPreferences>
) : Factory<IdentificationStepPreferences> {
    override fun get(): IdentificationStepPreferences {
        return Preconditions.checkNotNull(
                activitySubModule.provideIdentificationStepPreferences(sharedPreferencesProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
                activitySubModule: ActivitySubModule,
                sharedPreferencesProvider: Provider<SharedPreferences>
        ): IdentificationStepPreferencesFactory {
            return IdentificationStepPreferencesFactory(activitySubModule, sharedPreferencesProvider)
        }
    }
}