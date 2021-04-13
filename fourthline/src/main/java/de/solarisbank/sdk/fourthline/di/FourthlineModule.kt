package de.solarisbank.sdk.fourthline.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.fourthline.FourthlineViewModel
import de.solarisbank.sdk.fourthline.selfie.SelfieSharedViewModel
import de.solarisbank.sdk.fourthline.terms.TermsAndConditionsViewModel
import de.solarisbank.sdk.fourthline.welcome.WelcomeSharedViewModel

@RestrictTo(RestrictTo.Scope.LIBRARY)
class FourthlineModule {

    fun provideViewModelFactory(classProviderMap: Map<Class<out ViewModel>, Provider<ViewModel>>,
                                classSavedStateProviderMap: Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>): AssistedViewModelFactory {
        return AssistedViewModelFactory(classSavedStateProviderMap, classProviderMap)
    }

    fun provideTermsAndConditionsViewModel(savedStateHandle: SavedStateHandle): ViewModel {
        return TermsAndConditionsViewModel(savedStateHandle)
    }

    fun provideWelcomeSharedViewModel(savedStateHandle: SavedStateHandle): WelcomeSharedViewModel {
        return WelcomeSharedViewModel(savedStateHandle)
    }

    fun provideSelfieSharedViewModel(savedStateHandle: SavedStateHandle): ViewModel {
        return SelfieSharedViewModel(savedStateHandle)
    }

    fun provideFourthlineViewModel(savedStateHandle: SavedStateHandle): ViewModel {
        return FourthlineViewModel(savedStateHandle)
    }
}