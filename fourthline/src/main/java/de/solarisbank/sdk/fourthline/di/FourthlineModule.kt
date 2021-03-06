package de.solarisbank.sdk.fourthline.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.fourthline.feature.ui.terms.TermsAndConditionsViewModel
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomeSharedViewModel

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

    fun provideFourthlineViewModel(
            savedStateHandle: SavedStateHandle
    ): ViewModel {
        return FourthlineViewModel(savedStateHandle)
    }

    fun provideDocScanSharedViewModel(
            savedStateHandle: SavedStateHandle,
            personDataUseCase: PersonDataUseCase,
            kycInfoUseCase: KycInfoUseCase): ViewModel {
        return KycSharedViewModel(savedStateHandle, personDataUseCase, kycInfoUseCase)
    }
}