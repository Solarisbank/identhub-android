package de.solarisbank.sdk.fourthline.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

class FourthlineModuleAssistedViewModelFactory private constructor(
        private val fourthlineModule: FourthlineModule,
        private val mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>>,
        private val mapOfClassOfAndProviderOfSavedViewModelProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>>
        ) : Factory<AssistedViewModelFactory> {

    override fun get(): AssistedViewModelFactory {
        return Preconditions.checkNotNull(
                fourthlineModule.provideViewModelFactory(mapOfClassOfAndProviderOfViewModelProvider.get(), mapOfClassOfAndProviderOfSavedViewModelProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
                fourthlineModule: FourthlineModule,
                mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>>,
                mapOfClassOfAndProviderOfSavedViewModelProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>>
        ): FourthlineModuleAssistedViewModelFactory {
            return FourthlineModuleAssistedViewModelFactory(fourthlineModule, mapOfClassOfAndProviderOfViewModelProvider, mapOfClassOfAndProviderOfSavedViewModelProvider)
        }
    }
}