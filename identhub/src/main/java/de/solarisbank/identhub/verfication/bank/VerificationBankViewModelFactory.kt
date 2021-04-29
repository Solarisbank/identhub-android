package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider

class VerificationBankViewModelFactory(private val verificationBankModule: VerificationBankModule, private val identificationStepPreferencesProvider: Provider<IdentificationStepPreferences>, private val getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>, private val sessionUrlRepositoryProvider: Provider<SessionUrlRepository>) : Factory2<ViewModel, SavedStateHandle> {

    override fun create(savedStateHandle: SavedStateHandle): ViewModel {
        return verificationBankModule.provideVerificationBankActivityViewModel(savedStateHandle, identificationStepPreferencesProvider.get(), getIdentificationUseCaseProvider.get(), sessionUrlRepositoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun create(verificationBankModule: VerificationBankModule, identificationStepPreferencesProvider: Provider<IdentificationStepPreferences>, getIdentificationUseCaseProvider: Provider<GetIdentificationUseCase>, sessionUrlRepositoryProvider: Provider<SessionUrlRepository>): Factory2<ViewModel, SavedStateHandle> {
            return VerificationBankViewModelFactory(verificationBankModule, identificationStepPreferencesProvider, getIdentificationUseCaseProvider, sessionUrlRepositoryProvider)
        }
    }
}