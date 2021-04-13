package de.solarisbank.identhub.base

import de.solarisbank.identhub.di.IdentHubActivitySubcomponent
import de.solarisbank.identhub.di.IdenthubComponent
import de.solarisbank.sdk.core.BaseActivity

abstract class IdentHubActivity : BaseActivity() {

    val identHubActivitySubcomponent: IdentHubActivitySubcomponent by lazy {
        IdenthubComponent.getInstance(libraryComponent)
                .activitySubcomponent().create(activityComponent)
    }

    override fun injectMe() {
        super.injectMe()
        inject(identHubActivitySubcomponent)
    }

    protected abstract fun inject(identHubActivitySubcomponent: IdentHubActivitySubcomponent)
}