package de.solarisbank.identhub.identity.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.identity.IdentityModule
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider

class IdentitySummaryViewModelFactory(
        private val identityModule: IdentityModule,
        private val getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>
) : Factory2<ViewModel, SavedStateHandle> {
    override fun create(savedStateHandle: SavedStateHandle): ViewModel {
        return identityModule.provideIdentitySummaryViewModel(
                getIdentificationUseCaseProvider.get(),
                savedStateHandle
        )
    }

    companion object {
        @JvmStatic
        fun create(
                identityModule: IdentityModule,
                getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>
        ): IdentitySummaryViewModelFactory {
            return IdentitySummaryViewModelFactory(identityModule, getIdentificationUseCaseProvider)
        }
    }
}