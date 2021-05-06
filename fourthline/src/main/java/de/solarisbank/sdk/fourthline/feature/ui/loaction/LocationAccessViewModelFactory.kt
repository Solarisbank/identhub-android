package de.solarisbank.sdk.fourthline.feature.ui.loaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.fourthline.di.FourthlineModule


class LocationAccessViewModelFactory : Factory2<ViewModel, SavedStateHandle> {

    private val fourthlineModule: FourthlineModule

    private constructor(fourthlineModule: FourthlineModule) {
        this.fourthlineModule = fourthlineModule
    }

    override fun create(value: SavedStateHandle): ViewModel {
        return fourthlineModule.provideLocationAccessViewModel(value)
    }

    companion object {
        @JvmStatic
        fun create(fourthlineModule: FourthlineModule): LocationAccessViewModelFactory {
            return LocationAccessViewModelFactory(fourthlineModule)
        }
    }
}