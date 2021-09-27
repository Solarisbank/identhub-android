package de.solarisbank.identhub.contract

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.session.data.preferences.IdentificationStepPreferences
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider

class ContractViewModelFactory(
    private val contractModule: ContractModule,
    private val identificationStepPreferencesProvider: Provider<IdentificationStepPreferences>,
    private val sessionUrlRepositoryProvider: Provider<SessionUrlRepository>,
    private val getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>
    ) : Factory2<ViewModel, SavedStateHandle> {

    override fun create(savedStateHandle: SavedStateHandle): ViewModel {
        return contractModule.provideContractViewModel(
            savedStateHandle, identificationStepPreferencesProvider.get(),
            sessionUrlRepositoryProvider.get(),
            getIdentificationUseCaseProvider.get()
        )
    }

    companion object {
        @JvmStatic
        fun create(
            contractModule: ContractModule,
            identificationStepPreferencesProvider: Provider<IdentificationStepPreferences>,
            sessionUrlRepositoryProvider: Provider<SessionUrlRepository>,
            getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>
        ): Factory2<ViewModel, SavedStateHandle> {
            return ContractViewModelFactory(
                contractModule,
                identificationStepPreferencesProvider,
                sessionUrlRepositoryProvider,
                getIdentificationUseCaseProvider
            )
        }
    }
}