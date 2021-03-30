package de.solarisbank.identhub.fourthline.selfie

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.di.internal.Factory2
import de.solarisbank.identhub.fourthline.FourthlineModule

class SelfieSharedViewModelFactory(
        private val fourthlineModule: FourthlineModule
) : Factory2<ViewModel, SavedStateHandle> {
    override fun create(value: SavedStateHandle): ViewModel {
        return fourthlineModule.provideSelfieSharedViewModel(value)
    }

    companion object {
        @JvmStatic
        fun create(fourthlineModule: FourthlineModule): SelfieSharedViewModelFactory {
            return SelfieSharedViewModelFactory(fourthlineModule)
        }
    }
}