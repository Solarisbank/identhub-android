package de.solarisbank.sdk.fourthline.selfie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.view.viewBinding
import de.solarisbank.sdk.fourthline.FourthlineComponent
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.databinding.FragmentSelfieResultBinding

class SelfieResultFragment : FourthlineFragment() {
    private val binding: FragmentSelfieResultBinding by viewBinding { FragmentSelfieResultBinding.inflate(layoutInflater) }
    private val viewModel: SelfieSharedViewModel by lazy<SelfieSharedViewModel> { activityViewModels() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (viewModel.selfieResultBitmap != null) {
            binding.imageResult.setImageBitmap(viewModel.selfieResultBitmap)
        }

        binding.retryButton.setOnClickListener { viewModel.onRetakeButtonClicked() }
        binding.submitButton.setOnClickListener { viewModel.onConfirmButtonClicked() }
    }

    override fun inject(component: FourthlineComponent) {
        component.inject(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(): Fragment {
            return SelfieResultFragment()
        }
    }
}