package de.solarisbank.identhub.di

import de.solarisbank.identhub.contract.ContractActivity
import de.solarisbank.identhub.identity.IdentityActivity
import de.solarisbank.identhub.identity.summary.IdentitySummaryActivity
import de.solarisbank.identhub.intro.IntroActivity
import de.solarisbank.identhub.verfication.bank.VerificationBankActivity
import de.solarisbank.sdk.core.di.CoreActivityComponent

interface IdentHubActivitySubcomponent {

    fun inject(verificationBankActivity: VerificationBankActivity)
    fun inject(contractActivity: ContractActivity)
    fun inject(introActivity: IntroActivity?)
    fun inject(identityActivity: IdentityActivity?)
    fun inject(identitySummaryActivity: IdentitySummaryActivity?)

    interface Factory {
        fun create(activityComponent: CoreActivityComponent): IdentHubActivitySubcomponent
    }

    fun fragmentComponent(): FragmentComponent.Factory
}