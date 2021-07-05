package de.solarisbank.sdk.fourthline.feature.ui.terms.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.viewModels
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomeSharedViewModel

class WelcomeContainerFragment : FourthlineFragment() {

    private var startbutton: TextView? = null

    private val viewModel: WelcomeSharedViewModel by lazy<WelcomeSharedViewModel> { viewModels() }

    private val sharedViewModel: FourthlineViewModel by lazy<FourthlineViewModel> {
        activityViewModels()
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome_container, container, false)
                .also {
                    startbutton = it.findViewById(R.id.startButton)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        startbutton!!.setOnClickListener { sharedViewModel.navigateToSelfieFragment() }
    }

    override fun onDestroyView() {
        startbutton = null
        super.onDestroyView()
    }
}

