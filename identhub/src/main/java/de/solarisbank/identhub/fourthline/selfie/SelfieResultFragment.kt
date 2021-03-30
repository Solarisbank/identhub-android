package de.solarisbank.identhub.fourthline.selfie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.solarisbank.identhub.base.BaseFragment
import de.solarisbank.identhub.base.activityViewModels
import de.solarisbank.identhub.base.view.viewBinding
import de.solarisbank.identhub.databinding.FragmentSelfieResultBinding
import de.solarisbank.identhub.di.FragmentComponent

class SelfieResultFragment : BaseFragment() {
    private val binding: FragmentSelfieResultBinding by viewBinding(FragmentSelfieResultBinding::inflate)
    private val viewModel: SelfieSharedViewModel by lazy { activityViewModels() }

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

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(): Fragment {
            return SelfieResultFragment()
        }
    }
}