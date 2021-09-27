package de.solarisbank.identhub.session.feature.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.domain.IdentHubSessionUseCase
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

@RestrictTo(RestrictTo.Scope.LIBRARY)
class IdentHubSessionModule {

    fun provideViewModelFactory(
        saveStateViewModels: Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>,
        classProviderMap: Map<Class<out ViewModel>, Provider<ViewModel>>
    ): AssistedViewModelFactory {
        return AssistedViewModelFactory(saveStateViewModels, classProviderMap)
    }

    fun provideIdentHubSessionViewModel(
        identHubSessionUseCase: IdentHubSessionUseCase
    ): IdentHubSessionViewModel {
        return IdentHubSessionViewModel(identHubSessionUseCase)
    }

}