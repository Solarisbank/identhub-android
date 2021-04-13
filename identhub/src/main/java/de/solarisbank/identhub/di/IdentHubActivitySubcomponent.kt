package de.solarisbank.identhub.di

import de.solarisbank.identhub.identity.IdentityActivity
import de.solarisbank.identhub.identity.summary.IdentitySummaryActivity
import de.solarisbank.identhub.intro.IntroActivity
import de.solarisbank.sdk.core.di.CoreActivityComponent

interface IdentHubActivitySubcomponent {

    fun inject(introActivity: IntroActivity?)
    fun inject(identityActivity: IdentityActivity?)
    fun inject(identitySummaryActivity: IdentitySummaryActivity?)

    interface Factory {
        fun create(activityComponent: CoreActivityComponent): IdentHubActivitySubcomponent
    }

    fun fragmentComponent(): FragmentComponent.Factory
}