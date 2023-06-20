package de.solarisbank.sdk.fourthline.feature.ui.selfie

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.fourthline.vision.RecordingType
import com.fourthline.vision.selfie.*
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.sdk.data.dto.Customization
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.*
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.custom.PunchholeView
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.logger.IdLogger
import de.solarisbank.sdk.logger.IdLogger.Category.Fourthline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.core.component.inject
import timber.log.Timber


class SelfieFragment : SelfieScannerFragment(), IdenthubKoinComponent {

    private var punchhole: PunchholeView? = null
    private var selfieMask: ImageView? = null
    private var stepName: TextView? = null
    private var hintTextView: TextView? = null
    private var icon: ImageView? = null
    private var warningsLabel: TextView? = null
    private var livenessMask: ImageView? = null
    private var progressBar: ProgressBar? = null

    private val kycSharedViewModel: KycSharedViewModel by koinNavGraphViewModel(FourthlineFlow.navigationId)
    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineFlow.navigationId)

    private val customizationRepository: CustomizationRepository by inject()

    private var cleanupJob: Job? = null

    private val customization: Customization by lazy {
        customizationRepository.get()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        (view as ViewGroup).onLayoutMeasuredOnce {
            punchhole!!.punchholeRect = getFaceDetectionArea()
            punchhole!!.postInvalidate()
        }
    }

    override fun getConfig(): SelfieScannerConfig {
        return SelfieScannerConfig(
            debugModeEnabled = false,
            recordingType = RecordingType.VIDEO_ONLY,
            livenessCheckType = LivenessCheckType.HEAD_TURN
        )
    }

    override fun getFaceDetectionArea(): Rect {
        return Rect(selfieMask!!.left,
                selfieMask!!.top,
                selfieMask!!.right,
                selfieMask!!.bottom
        )
    }

    override fun getOverlayView(): View? {
        return LayoutInflater
                .from(requireContext())
                .inflate(R.layout.identhub_fragment_selfie, requireActivity().findViewById(R.id.content))
                .also {
                    punchhole = it.findViewById(R.id.punchhole)
                    selfieMask = it.findViewById(R.id.selfieMask)
                    stepName = it.findViewById(R.id.stepName)
                    icon = it.findViewById(R.id.imageView)
                    warningsLabel = it.findViewById(R.id.warningsLabel)
                    hintTextView = it.findViewById(R.id.hintTextView)
                    livenessMask = it.findViewById(R.id.livenessMask)
                    progressBar = it.findViewById(R.id.progressBar)
                    customizeView()
                }
    }

    private fun customizeView() {
        progressBar?.customize(customization)
    }

    override fun onFail(error: SelfieScannerError) {
        IdLogger.error(category = Fourthline, message = "Selfie error: ${error.name}")
        lifecycleScope.launch(Dispatchers.Main) {
            activityViewModel.onSelfieOutcome(SelfieOutcome.Failed(error.asString(requireContext())))
        }
    }

    override fun onStepUpdate(step: SelfieScannerStep) {
        Timber.d("onStepUpdate: ${step.name}")
        lifecycleScope.launch(Dispatchers.Main) {
            when (step) {
                SelfieScannerStep.SELFIE -> {
                    stepName!!.text = context?.resources?.getString(R.string.identhub_selfie_step_scanning)
                }
                SelfieScannerStep.TURN_HEAD_LEFT -> {
                    stepName!!.text = context?.resources?.getString(R.string.identhub_selfie_step_checking_liveness)
                    livenessMask!!.setImageResource(R.drawable.identhub_ic_liveness_left_direction)
                    selfieMask!!.visibility = View.INVISIBLE
                }
                SelfieScannerStep.TURN_HEAD_RIGHT -> {
                    stepName!!.text = context?.resources?.getString(R.string.identhub_selfie_step_checking_liveness)
                    livenessMask!!.setImageResource(R.drawable.identhub_ic_liveness_right_direction)
                    selfieMask!!.visibility = View.INVISIBLE
                }
            }
            hintTextView!!.text = step.asString(requireContext())
        }
    }

    override fun onSuccess(result: SelfieScannerResult) {
        lifecycleScope.launch(Dispatchers.Main) {
            IdLogger.info(category = Fourthline, message = "Selfie Capture Successful")
            icon?.visibility = View.VISIBLE
            icon?.setImageLevel(1)
            warningsLabel?.hide()
            stepName?.setText(R.string.identhub_selfie_scanner_scan_successful)
            stepName?.show()
            kycSharedViewModel.updateKycWithSelfieScannerResult(result)
            activityViewModel.onSelfieOutcome(SelfieOutcome.Success)
        }
    }

    override fun onWarnings(warnings: List<SelfieScannerWarning>) {
        Timber.d("onWarnings: ${warnings[0].name}")
        cleanupJob?.cancel()
        cleanupJob = lifecycleScope.launch(Dispatchers.Main) {
            warningsLabel!!.text = warnings[0].asString(requireContext())
            stepName?.visibility = View.GONE
            delay(3000)
            warningsLabel!!.text = ""
            stepName?.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        punchhole = null
        selfieMask = null
        stepName = null
        hintTextView = null
        icon = null
        warningsLabel = null
        livenessMask = null
        progressBar = null
        cleanupJob?.cancel()
        super.onDestroyView()
    }
}

sealed class SelfieOutcome {
    object Success: SelfieOutcome()
    data class Failed(val errorMessage: String): SelfieOutcome()
}