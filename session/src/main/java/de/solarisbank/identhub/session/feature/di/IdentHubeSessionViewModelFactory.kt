package de.solarisbank.identhub.session.feature.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class IdentHubeSessionViewModelFactory private constructor(
    private val identHubSessionModule: IdentHubSessionModule,
    private val saveStateViewModelMapProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>>,
    private val mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>>
) : Factory<AssistedViewModelFactory> {

    override fun get(): AssistedViewModelFactory {
        return identHubSessionModule.provideViewModelFactory(
                saveStateViewModelMapProvider.get(),
                mapOfClassOfAndProviderOfViewModelProvider.get()
        )
    }

    companion object {
        @JvmStatic
        fun create(
            identHubSessionModule: IdentHubSessionModule,
            saveStateViewModelMapProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>>,
            mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>>
        ): IdentHubeSessionViewModelFactory {
            return IdentHubeSessionViewModelFactory(
                    identHubSessionModule,
                    saveStateViewModelMapProvider,
                    mapOfClassOfAndProviderOfViewModelProvider
            )
        }
    }

}