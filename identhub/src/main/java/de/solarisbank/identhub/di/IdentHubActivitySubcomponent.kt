package de.solarisbank.identhub.di

import de.solarisbank.identhub.contract.ContractActivity
import de.solarisbank.identhub.identity.IdentityActivity
import de.solarisbank.identhub.verfication.bank.VerificationBankActivity
import de.solarisbank.sdk.feature.di.CoreActivityComponent

interface IdentHubActivitySubcomponent {

    fun inject(verificationBankActivity: VerificationBankActivity)
    fun inject(contractActivity: ContractActivity)
    fun inject(identityActivity: IdentityActivity?)

    interface Factory {
        fun create(activityComponent: CoreActivityComponent): IdentHubActivitySubcomponent
    }

    fun fragmentComponent(): FragmentComponent.Factory
}