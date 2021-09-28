package de.solarisbank.sdk.fourthline.feature.ui.selfie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.feature.base.BaseActivity
import de.solarisbank.sdk.feature.customization.ButtonStyle
import de.solarisbank.sdk.feature.customization.ImageViewTint
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel

class SelfieResultFragment : FourthlineFragment() {

    private var imageResult: ImageView? = null
    private var submitButton: Button? = null
    private var retryButton: Button? = null
    private var successImageView: ImageView? = null

    private val activityViewModel: FourthlineViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)
                .get(FourthlineViewModel::class.java)
    }

    private val kycSharedViewModel: KycSharedViewModel by lazy<KycSharedViewModel> {
        ViewModelProvider(requireActivity(), (requireActivity() as FourthlineActivity).viewModelFactory)[KycSharedViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_selfie_result, container, false)
                .also {
                    imageResult = it.findViewById(R.id.imageResult)
                    submitButton = it.findViewById(R.id.submitButton)
                    retryButton = it.findViewById(R.id.retryButton)
                    successImageView = it.findViewById(R.id.successImageView)
                    customizeUI()
                }
    }

    private fun customizeUI() {
        submitButton?.customize(customization)
        retryButton?.customize(customization, ButtonStyle.Alternative)
        successImageView?.customize(customization, ImageViewTint.Secondary)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        kycSharedViewModel.getSelfieResultCroppedBitmapLiveData().observe(viewLifecycleOwner) {
            imageResult!!.setImageBitmap(it)
        }

        retryButton!!.setOnClickListener {
            val bundle = Bundle().apply {
                putString(
                    FourthlineActivity.KEY_CODE,
                    FourthlineActivity.FOURTHLINE_SELFIE_RETAKE
                )
            }
            activityViewModel.resetFlowToWelcomeScreen(bundle)
        }
        submitButton!!.setOnClickListener { activityViewModel.navigateToDocTypeSelectionFragment() }
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onDestroyView() {
        imageResult = null
        submitButton = null
        retryButton = null
        successImageView = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(): Fragment {
            return SelfieResultFragment()
        }
    }
}