package de.solarisbank.sdk.fourthline.feature.ui.kyc.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.FourthlineFlow
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.domain.dto.KycUploadStatusDto
import de.solarisbank.sdk.fourthline.domain.dto.ZipCreationStateDto
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class KycUploadFragment : BaseFragment() {

    private val kycSharedViewModel: KycSharedViewModel by koinNavGraphViewModel(FourthlineFlow.navigationId)
    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineFlow.navigationId)

    private val kycUploadViewModel: KycUploadViewModel by viewModel()

    private var title: TextView? = null
    private var subtitle:  TextView? = null
    private var progressBar: ProgressBar? = null
    private var errorImage: ImageView? = null

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_kyc_upload, container, false)
            .also {
                title = it.findViewById(R.id.title)
                subtitle = it.findViewById(R.id.description)
                progressBar = it.findViewById(R.id.progressBar)
                errorImage = it.findViewById(R.id.errorImage)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kycUploadViewModel.uploadingStatus.observe(viewLifecycleOwner) { it.content?.let { statusDto -> setUiState(statusDto) }}
        //todo move to usecase
        val kycCreationState = kycSharedViewModel.createKycZip(requireContext().applicationContext)
        if (kycCreationState is ZipCreationStateDto.SUCCESS) {
            kycUploadViewModel.uploadKyc(kycCreationState.uri)
        } else {
            showGenericAlertFragment {  }
        }
    }

    override fun customizeView(view: View) {
        progressBar?.customize(customization)
    }

    private fun setUiState(statusDto: KycUploadStatusDto) {
        val state = getUploadDataForStatus(statusDto)
        Timber.d("setUiState: $state, $statusDto")
        when (statusDto) {
            is KycUploadStatusDto.ToNextStepSuccess -> {
                activityViewModel.onKycUploadOutcome(KycUploadOutcome.Success(nextStep = statusDto.nextStep))
            }
            is KycUploadStatusDto.FinishIdentSuccess -> {
                activityViewModel.onKycUploadOutcome(KycUploadOutcome.Success(identificationId = statusDto.id))
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
                alertTitle = getString(R.string.identhub_fourthline_kyc_result_title),
                alertMessage = getString(R.string.identhub_fourthline_kyc_result_label),
                positiveButtonLabel = getString(R.string.identhub_kyc_upload_success_button),
                positiveAlertButtonAction = null,
                negativeAlertButtonAction = null
            )
            is KycUploadStatusDto.ToNextStepSuccess -> UploadViewState(
                alertTitle = getString(R.string.identhub_fourthline_kyc_result_title),
                alertMessage = getString(R.string.identhub_fourthline_kyc_result_label),
                positiveButtonLabel = getString(R.string.identhub_kyc_upload_to_next_button),
                positiveAlertButtonAction = null,
                negativeAlertButtonAction = null
            )
            is KycUploadStatusDto.ProviderErrorNotFraud -> UploadViewState(
                alertTitle = getString(R.string.identhub_failure_no_fraud_headline),
                alertMessage = getString(R.string.identhub_failure_no_fraud_message),
                positiveButtonLabel = getString(R.string.identhub_failure_no_fraud_positive),
                negativeButtonLabel = getString(R.string.identhub_failure_no_fraud_negative),
                positiveAlertButtonAction =  { activityViewModel.restartFlow() },
                negativeAlertButtonAction = { activityViewModel.onKycUploadOutcome(KycUploadOutcome.Failed(
                    "Fourthline identification failed and user chose not to retry"
                )) }
            )
            is KycUploadStatusDto.ProviderErrorFraud -> UploadViewState(
                alertTitle = getString(R.string.identhub_failure_fraud_headline),
                alertMessage = getString(R.string.identhub_failure_fraud_message),
                positiveButtonLabel = getString(R.string.identhub_failure_fraud_button),
                positiveAlertButtonAction =  { activityViewModel.onKycUploadOutcome(KycUploadOutcome.Failed(
                    "Unrecoverable error happened when the uploaded data was processed"
                )) }
            )
            is KycUploadStatusDto.GenericError, is KycUploadStatusDto.PreconditionsFailedError -> UploadViewState(
                alertTitle = getString(R.string.identhub_kyc_upload_generic_error_title),
                alertMessage = getString(R.string.identhub_kyc_upload_generic_error_subtitle),
                positiveButtonLabel = getString(R.string.identhub_failure_no_fraud_negative),
                negativeButtonLabel = getString(R.string.identhub_kyc_upload_generic_error_button),
                positiveAlertButtonAction =  { activityViewModel.onKycUploadOutcome(KycUploadOutcome.Failed(
                    "Generic error when uploading the documents"
                )) },
                negativeAlertButtonAction = { activityViewModel.onKycUploadOutcome(KycUploadOutcome.RestartFlow) }
            )
        }
    }
}

sealed class KycUploadOutcome {
    data class Success(val nextStep: String? = null, val identificationId: String? = null): KycUploadOutcome()
    data class Failed(val message: String): KycUploadOutcome()
    object RestartFlow: KycUploadOutcome()
}