package de.solarisbank.identhub.verfication.phone

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.identity.IdentityModule
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class PhoneVerificationViewModelFactory(
    val identityModule: IdentityModule,
    val phoneVerificationUseCaseProvider: Provider<PhoneVerificationUseCase>
): Factory<ViewModel> {

    override fun get(): ViewModel {
        return identityModule.providePhoneVerificationViewModel(phoneVerificationUseCaseProvider.get())
    }
}