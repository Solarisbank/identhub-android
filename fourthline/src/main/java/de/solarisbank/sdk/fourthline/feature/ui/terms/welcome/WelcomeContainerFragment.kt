package de.solarisbank.sdk.fourthline.feature.ui.terms.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.isVisible
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel

class WelcomeContainerFragment : FourthlineFragment() {

    private var startbutton: Button? = null
    private var imageView: ImageView? = null
    private var firstTick: ImageView? = null
    private var secondTick: ImageView? = null

    private val sharedViewModel: FourthlineViewModel by lazy { activityViewModels() }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome_container, container, false)
                .also {
                    startbutton = it.findViewById(R.id.startButton)
                    imageView = it.findViewById(R.id.image)
                    firstTick = it.findViewById(R.id.firstTick)
                    secondTick = it.findViewById(R.id.secondTick)
                    customizeUI()
                }
    }

    private fun customizeUI() {
        imageView?.isVisible = customization.customFlags.shouldShowLargeImages
        startbutton?.customize(customization)
        firstTick?.customize(customization)
        secondTick?.customize(customization)
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
        imageView = null
        firstTick = null
        secondTick = null
        super.onDestroyView()
    }

    private fun showSelfieScanner() {
        sharedViewModel.navigateToSelfieFragment()
    }

    private fun handleErrors(saved: Bundle?) {
        val code = arguments?.getString(FourthlineActivity.KEY_CODE)
        val message = arguments?.getString(FourthlineActivity.KEY_MESSAGE)
        if (saved != null) {
            return
        }
        if (code == FourthlineActivity.FOURTHLINE_SCAN_FAILED) {
            showAlertFragment(
                getString(R.string.scanner_error_title),
                message ?: getString(R.string.scanner_error_unknown),
                getString(R.string.scanner_error_scan_button_retry),
                getString(R.string.scanner_error_scan_button_quit),
                { showSelfieScanner() },
                { sharedViewModel.setFourthlineIdentificationFailure() }
            )
        } else if (code == FourthlineActivity.FOURTHLINE_SELFIE_RETAKE) {
            showSelfieScanner()
        }
    }
}

