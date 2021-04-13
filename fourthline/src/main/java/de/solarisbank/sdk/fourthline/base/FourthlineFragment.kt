package de.solarisbank.sdk.fourthline.base

import android.os.Bundle
import de.solarisbank.sdk.core.BaseFragment
import de.solarisbank.sdk.fourthline.FourthlineActivity
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent


abstract class FourthlineFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val activityComponent = (requireActivity() as FourthlineActivity).activitySubcomponent
        inject(activityComponent.fragmentComponent().create())
        super.onCreate(savedInstanceState)
    }

    protected abstract fun inject(component: FourthlineFragmentComponent)
}