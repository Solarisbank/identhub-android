package de.solarisbank.sdk.fourthline.feature.ui.kyc.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.domain.dto.KycUploadStatusDto
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import timber.log.Timber
import java.io.File

class KycUploadFragment : FourthlineFragment() {

    private val activityViewModel: FourthlineViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)
                .get(FourthlineViewModel::class.java)
    }

    private val kycSharedViewModel: KycSharedViewModel by lazy<KycSharedViewModel> {
        ViewModelProvider(requireActivity(), (requireActivity() as FourthlineActivity).viewModelFactory)[KycSharedViewModel::class.java]
    }

    private val kycUploadViewModel: KycUploadViewModel by lazy<KycUploadViewModel> {
        ViewModelProvider(requireActivity(), (requireActivity() as FourthlineActivity).viewModelFactory)[KycUploadViewModel::class.java]
    }

    private var title: TextView? = null
    private var subtitle:  TextView? = null
    private var progressBar: ProgressBar? = null
    private var errorImage: ImageView? = null

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_kyc_upload, container, false)
                .also {
                    title = it.findViewById(R.id.title)
                    subtitle = it.findViewById(R.id.subtitle)
                    progressBar = it.findViewById(R.id.progressBar)
                    errorImage = it.findViewById(R.id.errorImage)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kycUploadViewModel.uploadingStatus.observe(viewLifecycleOwner) { it.content?.let { statusDto -> setUiState(statusDto) }}
        kycUploadViewModel.uploadKyc(File(kycSharedViewModel.kycURI!!))
    }

    private fun setUiState(statusDto: KycUploadStatusDto) {
        val state = getUploadDataForStatus(statusDto)
        Timber.d("setUiState: $state, $statusDto")
        when (statusDto) {
            is KycUploadStatusDto.ToNextStepSuccess -> {
                activityViewModel.navigateToUploadResultFragment(nextStep = statusDto.nextStep)
            }
             is KycUploadStatusDto.FinishIdentSuccess -> {
                 activityViewModel.navigateToUploadResultFragment(identificationId = statusDto.id)
             }
            is KycUploadStatusDto.ProviderErrorNotFraud,
            is KycUploadStatusDto.ProviderErrorFraud,
            is KycUploadStatusDto.PreconditionsFailedError,
            is KycUploadStatusDto.GenericError-> {
                progressBar!!.visibility = View.INVISIBLE
                errorImage!!.visibility = View.VISIBLE
                title!!.visibility = View.INVISIBLE
                subtitle!!.visibility = View.INVISIBLE
                showAlertFragment(
                    title = state.alertTitle,
                    message = state.alertMessage,
                    positiveLabel = state.positiveButtonLabel,
                    negativeLabel = state.negativeButtonLabel,
                    positiveAction = state.positiveAlertButtonAction!!,
                    negativeAction = state.negativeAlertButtonAction
                )
            }
        }
    }

    override fun onDestroyView() {
        title = null
        subtitle = null
        progressBar = null
        errorImage = null
        super.onDestroyView()
    }

    data class UploadViewState(
        val alertTitle: String,
        val alertMessage: String,
        val positiveButtonLabel: String,
        val negativeButtonLabel: String? = null,
        val positiveAlertButtonAction: (() -> Unit)?,
        val negativeAlertButtonAction: (() -> Unit)? = null
    )

    private fun getUploadDataForStatus(uploadStatusDto: KycUploadStatusDto): UploadViewState {
        return when (uploadStatusDto) {
            is KycUploadStatusDto.FinishIdentSuccess -> UploadViewState(
                alertTitle = getString(R.string.kyc_upload_success_title),
                alertMessage = getString(R.string.kyc_upload_success_subtitle),
                positiveButtonLabel = getString(R.string.kyc_upload_success_button),
                positiveAlertButtonAction = null,
                negativeAlertButtonAction = null
            )
            is KycUploadStatusDto.ToNextStepSuccess -> UploadViewState(
                alertTitle = getString(R.string.kyc_upload_to_next_title),
                alertMessage = getString(R.string.kyc_upload_to_next_subtitle),
                positiveButtonLabel = getString(R.string.kyc_upload_to_next_button),
                positiveAlertButtonAction = null,
                negativeAlertButtonAction = null
            )
            is KycUploadStatusDto.ProviderErrorNotFraud -> UploadViewState(
                alertTitle = getString(R.string.failure_no_fraud_headline),
                alertMessage = getString(R.string.failure_no_fraud_message),
                positiveButtonLabel = getString(R.string.failure_no_fraud_positive),
                negativeButtonLabel = getString(R.string.failure_no_fraud_negative),
                positiveAlertButtonAction =  { activityViewModel.resetFlowToPassingPossibility() },
                negativeAlertButtonAction = { activityViewModel.setFourthlineIdentificationFailure() }
            )
            is KycUploadStatusDto.ProviderErrorFraud -> UploadViewState(
                alertTitle = getString(R.string.failure_fraud_headline),
                alertMessage = getString(R.string.failure_fraud_message),
                positiveButtonLabel = getString(R.string.failure_fraud_button),
                positiveAlertButtonAction =  { activityViewModel.setFourthlineIdentificationFailure() }
            )
            is KycUploadStatusDto.GenericError, is KycUploadStatusDto.PreconditionsFailedError -> UploadViewState(
                alertTitle = getString(R.string.kyc_upload_generic_error_title),
                alertMessage = getString(R.string.kyc_upload_generic_error_subtitle),
                positiveButtonLabel = getString(R.string.kyc_upload_generic_error_button),
                negativeButtonLabel = getString(R.string.failure_no_fraud_negative),
                positiveAlertButtonAction =  { activityViewModel.resetFlowToPassingPossibility() },
                negativeAlertButtonAction = { activityViewModel.setFourthlineIdentificationFailure() }
            )
        }
    }
}