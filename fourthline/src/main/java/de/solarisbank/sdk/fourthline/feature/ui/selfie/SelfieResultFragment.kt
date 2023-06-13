package de.solarisbank.sdk.fourthline.feature.ui.selfie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.feature.customization.ButtonStyle
import de.solarisbank.sdk.feature.customization.ImageViewTint
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.FourthlineModule
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import org.koin.androidx.navigation.koinNavGraphViewModel

class SelfieResultFragment : BaseFragment() {

    private var imageResult: ImageView? = null
    private var submitButton: Button? = null
    private var retryButton: Button? = null
    private var successImageView: ImageView? = null

    private val kycSharedViewModel: KycSharedViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)
    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_selfie_result, container, false)
                .also {
                    imageResult = it.findViewById(R.id.imageResult)
                    submitButton = it.findViewById(R.id.submitButton)
                    retryButton = it.findViewById(R.id.retryButton)
                    successImageView = it.findViewById(R.id.successImageView)
                }
    }

    override fun customizeView(view: View) {
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
            activityViewModel.onSelfieResultOutcome(SelfieResultOutcome.Failed)
        }
        submitButton!!.setOnClickListener {
            activityViewModel.onSelfieResultOutcome(SelfieResultOutcome.Success)
        }
    }

    override fun onDestroyView() {
        imageResult = null
        submitButton = null
        retryButton = null
        successImageView = null
        super.onDestroyView()
    }
}

sealed class SelfieResultOutcome {
    object Success: SelfieResultOutcome()
    object Failed: SelfieResultOutcome()
}