package de.solarisbank.identhub.di

import de.solarisbank.identhub.contract.preview.ContractSigningPreviewFragment
import de.solarisbank.identhub.contract.sign.ContractSigningFragment
import de.solarisbank.identhub.identity.summary.IdentitySummaryFragment
import de.solarisbank.identhub.intro.IntroFragment
import de.solarisbank.identhub.progress.ProgressIndicatorFragment
import de.solarisbank.identhub.verfication.bank.VerificationBankFragment
import de.solarisbank.identhub.verfication.bank.error.VerificationBankErrorMessageFragment
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragment
import de.solarisbank.identhub.verfication.bank.success.VerificationBankSuccessMessageFragment
import de.solarisbank.identhub.verfication.phone.VerificationPhoneFragment
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorMessageFragment
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragment

interface FragmentComponent {
    fun inject(verificationPhoneFragment: VerificationPhoneFragment)
    fun inject(successMessageFragment: VerificationPhoneSuccessMessageFragment)
    fun inject(errorMessageFragment: VerificationPhoneErrorMessageFragment)
    fun inject(verificationBankFragment: VerificationBankFragment)
    fun inject(verificationBankExternalGatewayFragment: VerificationBankExternalGatewayFragment)
    fun inject(verificationBankSuccessMessageFragment: VerificationBankSuccessMessageFragment)
    fun inject(verificationBankErrorMessageFragment: VerificationBankErrorMessageFragment)
    fun inject(contractSigningFragment: ContractSigningFragment)
    fun inject(contractSigningPreviewFragment: ContractSigningPreviewFragment)
    fun inject(identitySummaryFragment: IdentitySummaryFragment)
    fun inject(progressIndicatorFragment: ProgressIndicatorFragment)
    fun inject(introFragment: IntroFragment)

    interface Factory {
        fun create(): FragmentComponent
    }
}