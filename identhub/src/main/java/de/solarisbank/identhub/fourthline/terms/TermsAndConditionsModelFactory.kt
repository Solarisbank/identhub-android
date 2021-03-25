package de.solarisbank.identhub.fourthline.terms

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.di.internal.Factory2
import de.solarisbank.identhub.fourthline.FourthlineModule

class TermsAndConditionsModelFactory(
        private val fourthlineModule: FourthlineModule
) : Factory2<ViewModel, SavedStateHandle> {
    override fun create(value: SavedStateHandle): ViewModel {
        return fourthlineModule.provideTermsAndConditionsViewModel(value)
    }

    companion object {
        @JvmStatic
        fun create(fourthlineModule: FourthlineModule): TermsAndConditionsModelFactory {
            return TermsAndConditionsModelFactory(fourthlineModule)
        }
    }
}