package de.solarisbank.identhub.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class ActivitySubModuleAssistedViewModelFactory(
    private val activitySubModule: ActivitySubModule,
    private val mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>>,
    private val mapOfClassOfAndProviderOfSavedViewModelProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>>
) : Factory<AssistedViewModelFactory> {

    override fun get(): AssistedViewModelFactory {
        return Preconditions.checkNotNull(
                activitySubModule.provideViewModelFactory(mapOfClassOfAndProviderOfViewModelProvider.get(), mapOfClassOfAndProviderOfSavedViewModelProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            activitySubModule: ActivitySubModule,
            mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>>,
            mapOfClassOfAndProviderOfSavedViewModelProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>>
        ): ActivitySubModuleAssistedViewModelFactory {
            return ActivitySubModuleAssistedViewModelFactory(activitySubModule, mapOfClassOfAndProviderOfViewModelProvider, mapOfClassOfAndProviderOfSavedViewModelProvider)
        }
    }
}