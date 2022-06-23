package de.solarisbank.identhub.base

import android.os.Bundle
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.sdk.feature.base.BaseFragment
import de.solarisbank.sdk.logger.IdLogger

abstract class IdentHubFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val activityComponent = (requireActivity() as IdentHubActivity).identHubActivitySubcomponent
        inject(activityComponent.fragmentComponent().create())
        super.onCreate(savedInstanceState)
    }

    protected abstract fun inject(component: FragmentComponent)
}