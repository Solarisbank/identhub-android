package de.solarisbank.identhub.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class ActivitySubModule {
    fun provideViewModelFactory(
        classProviderMap: Map<Class<out ViewModel>, Provider<ViewModel>>,
        classSavedStateProviderMap: Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>
    ): AssistedViewModelFactory {
        return AssistedViewModelFactory(classSavedStateProviderMap, classProviderMap)
    }
}