package de.solarisbank.sdk.feature.di

import android.app.Application
import android.content.Context
import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

@RestrictTo(RestrictTo.Scope.LIBRARY)
class LibraryModule(private val application: Application) {
    fun provideApplicationContext(): Context {
        return application
    }

    fun provideViewModelFactory(classProviderMap: Map<Class<out ViewModel>, Provider<ViewModel>>,
                                classSavedStateProviderMap: Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>): AssistedViewModelFactory {
        return AssistedViewModelFactory(classSavedStateProviderMap, classProviderMap)
    }
}