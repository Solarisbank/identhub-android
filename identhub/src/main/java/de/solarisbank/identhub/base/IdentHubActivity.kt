package de.solarisbank.identhub.base

import de.solarisbank.identhub.di.ActivitySubcomponent
import de.solarisbank.identhub.di.IdenthubComponent
import de.solarisbank.sdk.core.BaseActivity

abstract class IdentHubActivity : BaseActivity() {

    val activitySubcomponent: ActivitySubcomponent by lazy {
        IdenthubComponent.getInstance(libraryComponent)
                .activitySubcomponent().create(activityComponent)
    }

    override fun injectMe() {
        super.injectMe()
        inject(activitySubcomponent)
    }

    protected abstract fun inject(activitySubcomponent: ActivitySubcomponent)
}