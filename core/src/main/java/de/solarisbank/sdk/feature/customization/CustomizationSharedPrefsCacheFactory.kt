package de.solarisbank.sdk.feature.customization

import android.content.SharedPreferences
import de.solarisbank.sdk.feature.di.CoreModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class CustomizationSharedPrefsCacheFactory (
    private val coreModule: CoreModule,
    private val sharedPreferencesProvider: Provider<SharedPreferences>
) : Factory<CustomizationSharedPrefsStore> {

    override fun get(): CustomizationSharedPrefsStore {
        return coreModule.provideCustomizationSharedPrefsStore(sharedPreferencesProvider.get())
    }
}