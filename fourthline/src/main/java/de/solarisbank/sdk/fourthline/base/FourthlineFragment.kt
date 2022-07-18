package de.solarisbank.sdk.fourthline.base

import android.os.Bundle
import android.widget.Toast
import de.solarisbank.sdk.feature.base.BaseFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity

abstract class FourthlineFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val activityComponent = (requireActivity() as FourthlineActivity).activitySubcomponent
        inject(activityComponent.fragmentComponent().create())
        super.onCreate(savedInstanceState)
    }

    protected abstract fun inject(component: FourthlineFragmentComponent)

    private fun showErrorToast(message: String = "") {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}