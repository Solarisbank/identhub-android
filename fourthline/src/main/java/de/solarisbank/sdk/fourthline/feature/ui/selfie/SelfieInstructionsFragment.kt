package de.solarisbank.sdk.fourthline.feature.ui.selfie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import de.solarisbank.identhub.session.main.NewBaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.FourthlineModule
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.FOURTHLINE_SCAN_FAILED
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.FOURTHLINE_SELFIE_RETAKE
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.KEY_CODE
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.KEY_MESSAGE
import org.koin.androidx.navigation.koinNavGraphViewModel

class SelfieInstructionsFragment : NewBaseFragment() {

    private var startbutton: Button? = null

    private val sharedViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.identhub_fragment_selfie_instructions, container, false)
                .also {
                    startbutton = it.findViewById(R.id.startButton)
                    customizeUI()
                }
    }

    private fun customizeUI() {
        startbutton?.customize(customization)
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

    private fun showSelfieScanner() {
        sharedViewModel.onSelfieInstructionsOutcome(SelfieInstructionsOutcome.Success)
    }

    private fun handleErrors(saved: Bundle?) {
        val code = arguments?.getString(KEY_CODE)
        val message = arguments?.getString(KEY_MESSAGE)
        if (saved != null) {
            return
        }
        if (code == FOURTHLINE_SCAN_FAILED) {
            showAlertFragment(
                getString(R.string.identhub_scanner_error_title),
                message ?: getString(R.string.identhub_scanner_error_unknown),
                getString(R.string.identhub_scanner_error_scan_button_retry),
                getString(R.string.identhub_scanner_error_scan_button_quit),
                { showSelfieScanner() },
                { sharedViewModel.onSelfieInstructionsOutcome(
                    SelfieInstructionsOutcome.Failed(
                        "User could not take a selfie and aborted the flow"
                    )
                ) }
            )
        } else if (code == FOURTHLINE_SELFIE_RETAKE) {
            showSelfieScanner()
        }
    }
}

sealed class SelfieInstructionsOutcome {
    object Success: SelfieInstructionsOutcome()
    data class Failed(val message: String): SelfieInstructionsOutcome()
}

