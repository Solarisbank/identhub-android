package de.solarisbank.sdk.fourthline.feature.ui.selfie

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fourthline.vision.selfie.*
import de.solarisbank.sdk.feature.base.BaseActivity
import de.solarisbank.sdk.feature.customization.Customization
import de.solarisbank.sdk.feature.customization.CustomizationRepository
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.fourthline.*
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.FOURTHLINE_SCAN_FAILED
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.KEY_CODE
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.KEY_MESSAGE
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.custom.PunchholeView
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


class SelfieFragment : SelfieScannerFragment() {

    private var punchhole: PunchholeView? = null
    private var selfieMask: ImageView? = null
    private var stepName: TextView? = null
    private var hintTextView: TextView? = null
    private var icon: ImageView? = null
    private var warningsLabel: TextView? = null
    private var livenessMask: ImageView? = null
    private var progressBar: ProgressBar? = null

    private val activityViewModel: FourthlineViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)
                .get(FourthlineViewModel::class.java)
    }

    private val kycSharedViewModel: KycSharedViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as FourthlineActivity).viewModelFactory)[KycSharedViewModel::class.java]
    }

    lateinit var assistedViewModelFactory: AssistedViewModelFactory

    lateinit var customizationRepository: CustomizationRepository

    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var cleanupJob: Job? = null

    private val customization: Customization by lazy {
        customizationRepository.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val activityComponent = (requireActivity() as FourthlineActivity).activitySubcomponent
        inject(activityComponent.fragmentComponent().create())
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initView()
    }

    private fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, arguments)
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
            shouldRecordVideo = true,
            includeManualSelfiePolicy = false,
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
                .inflate(R.layout.fragment_selfie, requireActivity().findViewById(R.id.content))
                .also {
                    punchhole = it.findViewById(R.id.punchhole)
                    selfieMask = it.findViewById(R.id.selfieMask)
                    stepName = it.findViewById(R.id.stepName)
                    icon = it.findViewById(R.id.imageView)
                    warningsLabel = it.findViewById(R.id.warningsLabel)
                    hintTextView = it.findViewById(R.id.hintTextView)
                    livenessMask = it.findViewById(R.id.livenessMask)
                    progressBar = it.findViewById(R.id.progressBar)
                    customizeUI()
                }
    }

    private fun customizeUI() {
        progressBar?.customize(customization)
    }

    override fun onFail(error: SelfieScannerError) {
        Timber.d("onFail: ${error.name}")
        lifecycleScope.launch(Dispatchers.Main) {
            val bundle = Bundle().apply {
                putString(KEY_CODE, FOURTHLINE_SCAN_FAILED)
                putString(KEY_MESSAGE, error.asString(requireContext()))
            }
            activityViewModel.resetFlowToWelcomeScreen(bundle)
        }
    }

    override fun onStepUpdate(step: SelfieScannerStep) {
        Timber.d("onStepUpdate: ${step.name}")
        lifecycleScope.launch(Dispatchers.Main) {
            when (step) {
                SelfieScannerStep.SELFIE -> {
                    stepName!!.text = context?.resources?.getString(R.string.selfie_step_scanning)
                }
                SelfieScannerStep.TURN_HEAD_LEFT -> {
                    stepName!!.text = context?.resources?.getString(R.string.selfie_step_checking_liveness)
                    livenessMask!!.setImageResource(R.drawable.ic_liveness_left_direction)
                }
                SelfieScannerStep.TURN_HEAD_RIGHT -> {
                    stepName!!.text = context?.resources?.getString(R.string.selfie_step_checking_liveness)
                    livenessMask!!.setImageResource(R.drawable.ic_liveness_right_direction)
                }
            }
            hintTextView!!.text = step.asString(requireContext())
        }
    }

    override fun onSuccess(result: SelfieScannerResult) {
        Timber.d("onSuccess()")

        lifecycleScope.launch(Dispatchers.Main) {
            icon?.visibility = View.VISIBLE
            icon?.setImageLevel(1)
            warningsLabel?.hide()
            stepName?.setText(R.string.selfie_scanner_scan_successful)
            stepName?.show()
            kycSharedViewModel.updateKycWithSelfieScannerResult(result)
            activityViewModel.navigateToSelfieResultFragment()
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

    fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

}