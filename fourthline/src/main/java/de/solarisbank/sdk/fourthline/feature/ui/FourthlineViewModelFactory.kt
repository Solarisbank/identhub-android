package de.solarisbank.sdk.fourthline.feature.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.fourthline.di.FourthlineModule
import de.solarisbank.sdk.fourthline.domain.step.parameters.FourthlineStepParametersUseCase

class FourthlineViewModelFactory private constructor(
        private val fourthlineModule: FourthlineModule,
        private val fourthlineStepParametersUseCase: FourthlineStepParametersUseCase
) : Factory2<ViewModel, SavedStateHandle> {

    override fun create(savedStateHandle: SavedStateHandle): ViewModel {
        return fourthlineModule.provideFourthlineViewModel(
            savedStateHandle,
            fourthlineStepParametersUseCase
        )
    }

    companion object {
        @JvmStatic
        fun create(
                fourthlineModule: FourthlineModule,
                fourthlineStepParametersUseCase: FourthlineStepParametersUseCase
        ): FourthlineViewModelFactory {
            return FourthlineViewModelFactory(fourthlineModule, fourthlineStepParametersUseCase)
        }
    }
}

