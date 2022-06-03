package de.solarisbank.identhub.session.feature.di

import android.app.Application
import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

@RestrictTo(RestrictTo.Scope.LIBRARY)
class IdentHubSessionModule {

    fun provideViewModelFactory(
        saveStateViewModels: Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>,
    ): AssistedViewModelFactory {
        return AssistedViewModelFactory(saveStateViewModels, emptyMap())
    }

    fun provideIdentHubSessionViewModel(
        savedStateHandle: SavedStateHandle,
        application: Application
    ): IdentHubSessionViewModel {
        return IdentHubSessionViewModel.getInstance(
            savedStateHandle,
            application
        )
    }

}