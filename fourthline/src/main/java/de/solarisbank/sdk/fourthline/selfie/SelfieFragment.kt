package de.solarisbank.sdk.fourthline.selfie

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fourthline.vision.selfie.*
import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.core.view.viewBinding
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.fourthline.FourthlineComponent
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.asString
import de.solarisbank.sdk.fourthline.databinding.FragmentSelfieBinding
import de.solarisbank.sdk.fourthline.onLayoutMeasuredOnce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SelfieFragment : SelfieScannerFragment() {
    private val binding: FragmentSelfieBinding by viewBinding { FragmentSelfieBinding.inflate(layoutInflater) }
    private val viewModel: SelfieSharedViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)[SelfieSharedViewModel::class.java]
    }

    lateinit var assistedViewModelFactory: AssistedViewModelFactory

    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var cleanupJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val activityComponent = (requireActivity() as BaseActivity).activityComponent
        inject(FourthlineComponent.getInstance(activityComponent))
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
        binding.takeSnapshot.setOnClickListener { takeSnapshot() }
        binding.root.onLayoutMeasuredOnce {
            binding.punchhole.punchholeRect = getFaceDetectionArea()
            binding.punchhole.postInvalidate()
        }
    }

    override fun getConfig(): SelfieScannerConfig {
        return SelfieScannerConfig(false, shouldRecordVideo = false, includeManualSelfiePolicy = true, livenessCheckType = LivenessCheckType.HEAD_TURN)
    }

    override fun getFaceDetectionArea(): Rect {
        return Rect(binding.selfieMask.left,
                binding.selfieMask.top,
                binding.selfieMask.right,
                binding.selfieMask.bottom
        )
    }

    override fun getOverlayView(): View? {
        return binding.root
    }

    override fun onFail(error: SelfieScannerError) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (error == SelfieScannerError.TIMEOUT) {
                binding.takeSnapshot.visibility = View.VISIBLE
            }
            Toast.makeText(requireContext(), error.asString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onStepUpdate(step: SelfieScannerStep) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.stepLabel.text = step.asString()
        }
    }

    override fun onSuccess(result: SelfieScannerResult) {
        viewModel.selfieResultBitmap = result.image.cropped

        lifecycleScope.launch(Dispatchers.Main) {
            binding.icon.visibility = View.VISIBLE
            binding.icon.setImageLevel(1)
            binding.warningsLabel.setText(R.string.selfie_scanner_scan_successful)
        }

        viewModel.success()
    }

    override fun onWarnings(warnings: List<SelfieScannerWarning>) {
        cleanupJob?.cancel()
        cleanupJob = lifecycleScope.launch(Dispatchers.Main) {
            binding.icon.visibility = View.VISIBLE
            binding.warningsLabel.text = warnings[0].asString()
            binding.icon.setImageLevel(0)

            delay(500)
            binding.warningsLabel.text = ""
            binding.icon.visibility = View.GONE
        }
    }

    fun inject(component: FourthlineComponent) {
        component.inject(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(): Fragment {
            return SelfieFragment()
        }
    }
}