package de.solarisbank.identhub.identity

import de.solarisbank.identhub.domain.verification.bank.ProcessingVerificationUseCase
import de.solarisbank.identhub.verfication.bank.gateway.processing.ProcessingVerificationViewModel
import de.solarisbank.identhub.verfication.phone.PhoneVerificationUseCase
import de.solarisbank.identhub.verfication.phone.PhoneVerificationViewModel
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessViewModel

class IdentityModule {


    fun providePhoneVerificationViewModel(
        phoneVerificationUseCase: PhoneVerificationUseCase
    ): PhoneVerificationViewModel {
        return PhoneVerificationViewModel(phoneVerificationUseCase)
    }

    fun provideVerificationPhoneSuccessViewModel(): VerificationPhoneSuccessViewModel {
        return VerificationPhoneSuccessViewModel()
    }

    fun provideProcessingVerificationViewModel(
        processingVerificationUseCase: ProcessingVerificationUseCase
    ): ProcessingVerificationViewModel {
        return ProcessingVerificationViewModel(processingVerificationUseCase)
    }



}