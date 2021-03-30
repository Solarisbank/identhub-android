package de.solarisbank.identhub.fourthline.welcome

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.di.internal.Factory2
import de.solarisbank.identhub.fourthline.FourthlineModule

class WelcomeViewModelFactory : Factory2<ViewModel, SavedStateHandle> {

    private val fourthlineModule: FourthlineModule

    private constructor(fourthlineModule: FourthlineModule) {
        this.fourthlineModule = fourthlineModule
    }

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