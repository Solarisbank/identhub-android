package de.solarisbank.identhub.di

import de.solarisbank.identhub.contract.preview.ContractSigningPreviewFragment
import de.solarisbank.identhub.contract.sign.ContractSigningFragment
import de.solarisbank.identhub.progress.ProgressIndicatorFragment
import de.solarisbank.identhub.verfication.bank.VerificationBankIbanFragment
import de.solarisbank.identhub.verfication.bank.gateway.VerificationBankExternalGatewayFragment
import de.solarisbank.identhub.verfication.phone.PhoneVerificationFragment
import de.solarisbank.identhub.verfication.phone.success.VerificationPhoneSuccessMessageFragment

interface FragmentComponent {
    fun inject(phoneVerificationFragment: PhoneVerificationFragment)
    fun inject(successMessageFragment: VerificationPhoneSuccessMessageFragment)
    fun inject(verificationBankIbanFragment: VerificationBankIbanFragment)
    fun inject(verificationBankExternalGatewayFragment: VerificationBankExternalGatewayFragment)
    fun inject(contractSigningFragment: ContractSigningFragment)
    fun inject(contractSigningPreviewFragment: ContractSigningPreviewFragment)
    fun inject(progressIndicatorFragment: ProgressIndicatorFragment)

    interface Factory {
        fun create(): FragmentComponent
    }
}