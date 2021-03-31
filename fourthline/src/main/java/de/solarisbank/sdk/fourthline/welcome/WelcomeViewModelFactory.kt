package de.solarisbank.sdk.fourthline.welcome

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.fourthline.FourthlineModule

class WelcomeViewModelFactory(private val fourthlineModule: FourthlineModule) : Factory2<ViewModel, SavedStateHandle> {

    override fun create(value: SavedStateHandle): WelcomeSharedViewModel {
        return fourthlineModule.provideWelcomeSharedViewModel(value)
    }

    companion object {
        @JvmStatic
        fun create(fourthlineModule: FourthlineModule): WelcomeViewModelFactory {
            return WelcomeViewModelFactory(fourthlineModule)
        }
    }
}