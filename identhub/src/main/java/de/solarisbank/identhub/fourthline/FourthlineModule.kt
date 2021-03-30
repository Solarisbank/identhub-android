package de.solarisbank.identhub.fourthline

import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.fourthline.selfie.SelfieSharedViewModel
import de.solarisbank.identhub.fourthline.terms.TermsAndConditionsViewModel
import de.solarisbank.identhub.fourthline.welcome.WelcomeSharedViewModel

@RestrictTo(RestrictTo.Scope.LIBRARY)
class FourthlineModule {

    fun provideTermsAndConditionsViewModel(savedStateHandle: SavedStateHandle): ViewModel {
        return TermsAndConditionsViewModel(savedStateHandle)
    }

    fun provideWelcomeSharedViewModel(savedStateHandle: SavedStateHandle): WelcomeSharedViewModel {
        return WelcomeSharedViewModel(savedStateHandle)
    }

    fun provideSelfieSharedViewModel(savedStateHandle: SavedStateHandle): ViewModel {
        return SelfieSharedViewModel(savedStateHandle)
    }
}