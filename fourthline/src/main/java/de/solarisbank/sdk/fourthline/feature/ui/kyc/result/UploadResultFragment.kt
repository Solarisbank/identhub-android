package de.solarisbank.sdk.fourthline.feature.ui.kyc.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import de.solarisbank.identhub.session.main.NewBaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.FourthlineModule
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.IDENTIFICATION_ID
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.NEXT_STEP_ARG
import org.koin.androidx.navigation.koinNavGraphViewModel

class UploadResultFragment : NewBaseFragment() {

    private var quitButton: Button? = null
    private var indicator: ImageView? = null

    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.identhub_fragment_upload_result, container, false).also {
            quitButton = it.findViewById(R.id.quitButton)
            indicator = it.findViewById(R.id.indicator)
            customizeUI()
        }
    }

    private fun customizeUI() {
        indicator?.customize(customization)
        quitButton?.customize(customization)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quitButton?.setOnClickListener { sendOutcome() }
    }

    private fun sendOutcome() {
        activityViewModel.onUploadResultOutcome(
            UploadResultOutcome(
                nextStep = arguments?.getString(NEXT_STEP_ARG),
                identificationId = arguments?.getString(IDENTIFICATION_ID)
            )
        )
    }

    override fun onDestroyView() {
        quitButton = null
        indicator = null
        super.onDestroyView()
    }
}

data class UploadResultOutcome(val identificationId: String?, val nextStep: String?)