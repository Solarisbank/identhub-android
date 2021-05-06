package de.solarisbank.sdk.fourthline.feature.ui.terms

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.fourthline.di.FourthlineModule

class TermsAndConditionsViewModelFactory(
        private val fourthlineModule: FourthlineModule
) : Factory2<ViewModel, SavedStateHandle> {
    override fun create(value: SavedStateHandle): ViewModel {
        return fourthlineModule.provideTermsAndConditionsViewModel(value)
    }

    companion object {
        @JvmStatic
        fun create(fourthlineModule: FourthlineModule): TermsAndConditionsViewModelFactory {
            return TermsAndConditionsViewModelFactory(fourthlineModule)
        }
    }
}