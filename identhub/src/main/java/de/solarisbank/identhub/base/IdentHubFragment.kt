package de.solarisbank.identhub.base

import android.os.Bundle
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.sdk.core.BaseFragment

abstract class IdentHubFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val activityComponent = (requireActivity() as IdentHubActivity).activitySubcomponent
        inject(activityComponent.fragmentComponent().create())
        super.onCreate(savedInstanceState)
    }

    protected abstract fun inject(component: FragmentComponent)
}