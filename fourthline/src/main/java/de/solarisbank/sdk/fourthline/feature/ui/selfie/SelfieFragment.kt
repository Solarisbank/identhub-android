package de.solarisbank.sdk.fourthline.feature.ui.selfie

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fourthline.vision.selfie.*
import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.asString
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.custom.PunchholeView
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.fourthline.onLayoutMeasuredOnce
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

    private val activityViewModel: FourthlineViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)
                .get(FourthlineViewModel::class.java)
    }

    private val kycSharedViewModel: KycSharedViewModel by lazy<KycSharedViewModel> {
        ViewModelProvider(requireActivity(), (requireActivity() as FourthlineActivity).viewModelFactory)[KycSharedViewModel::class.java]
    }

    lateinit var assistedViewModelFactory: AssistedViewModelFactory

    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var cleanupJob: Job? = null

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
        return SelfieScannerConfig(false, shouldRecordVideo = false, includeManualSelfiePolicy = true, livenessCheckType = LivenessCheckType.HEAD_TURN)
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
                }
    }

    override fun onFail(error: SelfieScannerError) {
        Timber.d("onFail: ${error.name}")
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), error.asString(requireContext()), Toast.LENGTH_LONG).show()
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
            icon!!.visibility = View.VISIBLE
            icon!!.setImageLevel(1)
            warningsLabel!!.setText(R.string.selfie_scanner_scan_successful)
            kycSharedViewModel.updateKycWithSelfieScannerResult(result)
            activityViewModel.navigateToSelfieResultFragment()
        }
    }

    override fun onWarnings(warnings: List<SelfieScannerWarning>) {
        Timber.d("onWarnings: ${warnings[0].name}")
        cleanupJob?.cancel()
        cleanupJob = lifecycleScope.launch(Dispatchers.Main) {
            icon!!.visibility = View.VISIBLE
            warningsLabel!!.text = warnings[0].asString(requireContext())
            icon!!.setImageLevel(0)
            delay(500)
            warningsLabel!!.text = ""
            icon!!.visibility = View.GONE
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
        super.onDestroyView()
    }

    fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

}