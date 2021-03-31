package de.solarisbank.sdk.fourthline.base

import android.os.Bundle
import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.core.BaseFragment
import de.solarisbank.sdk.fourthline.FourthlineComponent

abstract class FourthlineFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val activityComponent = (requireActivity() as BaseActivity).activityComponent
        inject(FourthlineComponent.getInstance(activityComponent))
        super.onCreate(savedInstanceState)
    }

    protected abstract fun inject(component: FourthlineComponent)
}