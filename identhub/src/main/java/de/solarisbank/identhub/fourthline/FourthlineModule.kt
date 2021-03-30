package de.solarisbank.identhub.fourthline

import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.fourthline.selfie.SelfieSharedViewModel
import de.solarisbank.identhub.fourthline.terms.TermsAndConditionsViewModel

@RestrictTo(RestrictTo.Scope.LIBRARY)
class FourthlineModule {

    fun provideTermsAndConditionsViewModel(savedStateHandle: SavedStateHandle): ViewModel {
        return TermsAndConditionsViewModel(savedStateHandle)
    }

    fun provideSelfieSharedViewModel(savedStateHandle: SavedStateHandle): ViewModel {
        return SelfieSharedViewModel(savedStateHandle)
    }
}