package de.solarisbank.sdk.fourthline

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.fourthline.di.FourthlineModule

class FourthlineViewModelFactory(
        private val fourthlineModule: FourthlineModule
) : Factory2<ViewModel, SavedStateHandle> {

    override fun create(value: SavedStateHandle): ViewModel {
        return fourthlineModule.provideFourthlineViewModel(value)
    }

    companion object {
        @JvmStatic
        fun create(fourthlineModule: FourthlineModule): FourthlineViewModelFactory {
            return FourthlineViewModelFactory(fourthlineModule)
        }
    }
}

