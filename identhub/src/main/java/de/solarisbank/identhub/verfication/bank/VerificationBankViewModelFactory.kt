package de.solarisbank.identhub.verfication.bank

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.config.InitializationInfoRepository
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider

class VerificationBankViewModelFactory(
    private val verificationBankModule: VerificationBankModule,
    private val sessionUrlRepositoryProvider: Provider<SessionUrlRepository>,
    private val initializationInfoRepositoryProvider: Provider<InitializationInfoRepository>
    ) : Factory2<ViewModel, SavedStateHandle> {

    override fun create(savedStateHandle: SavedStateHandle): ViewModel {
        return verificationBankModule.provideVerificationBankActivityViewModel(
            savedStateHandle,
            sessionUrlRepositoryProvider.get(),
            initializationInfoRepositoryProvider.get()
        )
    }
}