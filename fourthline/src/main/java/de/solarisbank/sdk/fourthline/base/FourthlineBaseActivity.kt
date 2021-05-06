package de.solarisbank.sdk.fourthline.base

import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.fourthline.di.FourthlineActivitySubcomponent
import de.solarisbank.sdk.fourthline.di.FourthlineComponent

abstract class FourthlineBaseActivity : BaseActivity() {

    val activitySubcomponent: FourthlineActivitySubcomponent by lazy {
        FourthlineComponent.getInstance(libraryComponent)
                .activitySubcomponent().create(activityComponent)
    }

    override fun injectMe() {
        super.injectMe()
        inject(activitySubcomponent)
    }

    protected abstract fun inject(activitySubcomponent: FourthlineActivitySubcomponent)
}