package de.solarisbank.sdk.fourthline.feature.ui.kyc.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.feature.base.BaseActivity
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.IDENTIFICATION_ID
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.NEXT_STEP_ARG
import timber.log.Timber

class UploadResultFragment : FourthlineFragment() {

    private var quitButton: Button? = null
    private var imageView: ImageView? = null
    private var indicator: ImageView? = null

    private val activityViewModel: FourthlineViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)[FourthlineViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.identhub_fragment_upload_result, container, false).also {
            quitButton = it.findViewById(R.id.quitButton)
            imageView = it.findViewById(R.id.resultImageView)
            indicator = it.findViewById(R.id.indicator)
            customizeUI()
        }
    }

    private fun customizeUI() {
        imageView?.isVisible = customization.customFlags.shouldShowLargeImages
        indicator?.customize(customization)
        quitButton?.customize(customization)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quitButton?.setOnClickListener {
            arguments?.getString(NEXT_STEP_ARG)?.let { moveToNextStep(it) }?:run{
                arguments?.getString(IDENTIFICATION_ID)?.let {
                    activityViewModel.setFourthlineIdentificationSuccessful(it)
                }
            }
        }
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    private fun moveToNextStep(nextStep: String) {
        Timber.d("moveToNextStep : $nextStep")
        activityViewModel.postDynamicNavigationNextStep(nextStep)
    }

    override fun onDestroyView() {
        quitButton = null
        imageView = null
        indicator = null
        super.onDestroyView()
    }

}