package de.solarisbank.identhub.di

import de.solarisbank.identhub.contract.preview.ContractSigningPreviewFragment
import de.solarisbank.identhub.contract.sign.ContractSigningFragment
import de.solarisbank.identhub.intro.IntroFragment
import de.solarisbank.identhub.verfication.bank.VerificationBankIntroFragment
import de.solarisbank.identhub.progress.ProgressIndicatorFragment
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanFragment
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragment
import de.solarisbank.identhub.verfication.phone.VerificationPhoneFragment
import de.solarisbank.identhub.verfication.phone.error.VerificationPhoneErrorMessageFragment
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragment

interface FragmentComponent {
    fun inject(verificationPhoneFragment: VerificationPhoneFragment)
    fun inject(successMessageFragment: VerificationPhoneSuccessMessageFragment)
    fun inject(errorMessageFragment: VerificationPhoneErrorMessageFragment)
    fun inject(verificationBankIbanFragment: VerificationBankIbanFragment)
    fun inject(verificationBankExternalGatewayFragment: VerificationBankExternalGatewayFragment)
    fun inject(contractSigningFragment: ContractSigningFragment)
    fun inject(contractSigningPreviewFragment: ContractSigningPreviewFragment)
    fun inject(progressIndicatorFragment: ProgressIndicatorFragment)
    fun inject(introFragment: IntroFragment)
    fun inject(verificationBankIntroFragment: VerificationBankIntroFragment)

    interface Factory {
        fun create(): FragmentComponent
    }
}