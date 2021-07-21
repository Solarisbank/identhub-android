package de.solarisbank.sdk.fourthline.feature.ui.terms.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.viewModels
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.FOURTHLINE_SELFIE_SCAN_FAILED
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.KEY_ERROR_CODE
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
        handleErrors(savedInstanceState)
    }

    private fun initView() {
        startbutton!!.setOnClickListener { showSelfieScanner() }
    }

    override fun onDestroyView() {
        startbutton = null
        super.onDestroyView()
    }

    private fun handleErrors(saved: Bundle?) {
        val errorCode = arguments?.getString(KEY_ERROR_CODE)
        if (saved != null) {
            return
        }
        if (errorCode == FOURTHLINE_SELFIE_SCAN_FAILED) {
            showAlertFragment(
                getString(R.string.selfie_scan_error),
                getString(R.string.selfie_error_scan_timeout_message),
                getString(R.string.selfie_error_scan_timeout_retry),
                getString(R.string.selfie_error_scan_timeout_quit),
                { showSelfieScanner() },
                { sharedViewModel.setFourthlineIdentificationFailure() }
            )
        }
    }

    private fun showSelfieScanner() {
        sharedViewModel.navigateToSelfieFragment()
    }
}

