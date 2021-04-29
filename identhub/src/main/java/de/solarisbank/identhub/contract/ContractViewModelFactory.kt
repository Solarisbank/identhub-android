package de.solarisbank.identhub.contract

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider

class ContractViewModelFactory(private val contractModule: ContractModule, private val identificationStepPreferencesProvider: Provider<IdentificationStepPreferences>, private val sessionUrlRepositoryProvider: Provider<SessionUrlRepository>) : Factory2<ViewModel, SavedStateHandle> {
    override fun create(savedStateHandle: SavedStateHandle): ViewModel {
        return contractModule.provideContractViewModel(savedStateHandle, identificationStepPreferencesProvider.get(), sessionUrlRepositoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun create(contractModule: ContractModule, identificationStepPreferencesProvider: Provider<IdentificationStepPreferences>, sessionUrlRepositoryProvider: Provider<SessionUrlRepository>): Factory2<ViewModel, SavedStateHandle> {
            return ContractViewModelFactory(contractModule, identificationStepPreferencesProvider, sessionUrlRepositoryProvider)
        }
    }
}