package de.solarisbank.sdk.fourthline.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

class FourthlineActivitySubModule {

    fun provideViewModelFactory(
            classProviderMap: Map<Class<out ViewModel?>?, Provider<ViewModel?>?>?,
            classSavedStateProviderMap: Map<Class<out ViewModel?>?, Factory2<ViewModel?, SavedStateHandle?>?>?
    ): AssistedViewModelFactory {
        return AssistedViewModelFactory(classSavedStateProviderMap, classProviderMap)
    }

}