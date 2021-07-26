package de.solarisbank.sdk.fourthline.feature.ui.kyc.upload

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.identhub.data.network.getErrorType
import de.solarisbank.identhub.router.NEXT_STEP_KEY
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.utils.isServiceRunning
import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.core.result.Type
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.service.kyc.upload.KycUploadService
import de.solarisbank.sdk.fourthline.feature.service.kyc.upload.KycUploadService.Companion.KYC_ZIPPER_URI
import de.solarisbank.sdk.fourthline.feature.service.kyc.upload.KycUploadServiceBinder
import de.solarisbank.sdk.fourthline.feature.service.kyc.upload.KycUploadStatus
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import timber.log.Timber

class KycUploadFragment : FourthlineFragment() {

    private val activityViewModel: FourthlineViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)
                .get(FourthlineViewModel::class.java)
    }

    private val kycSharedViewModel: KycSharedViewModel by lazy<KycSharedViewModel> {
        ViewModelProvider(requireActivity(), (requireActivity() as FourthlineActivity).viewModelFactory)[KycSharedViewModel::class.java]
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bound = true
            this@KycUploadFragment.binder = (service as KycUploadServiceBinder)
            this@KycUploadFragment.binder!!.uploadingStatus.observe(this@KycUploadFragment, uploadingObserver)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            binder?.uploadingStatus?.removeObserver(uploadingObserver)
            bound = false
        }
    }

    private val uploadingObserver = Observer<KycUploadStatus> {
        setUiState(it)
    }

    private var title: TextView? = null
    private var subtitle:  TextView? = null
    private var progressBar: ProgressBar? = null
    private var resultImageView: ImageView? = null
    private var submitButton: TextView? = null

    private var bound: Boolean = false
    private var binder: KycUploadServiceBinder? = null



    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_kyc_upload, container, false)
                .also {
                    title = it.findViewById(R.id.title)
                    subtitle = it.findViewById(R.id.subtitle)
                    progressBar = it.findViewById(R.id.progressBar)
                    resultImageView = it.findViewById(R.id.resultImageView)
                    submitButton = it.findViewById(R.id.quitButton)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isServiceRunning(requireContext())) {
            startKycUploadService()
        }
        bindKycUploadService()
    }

    private fun setUiState(status: KycUploadStatus) {
        when(status) {
            is KycUploadStatus.ProviderError -> {
                showErrorAlert(status.isFraud)
            }
            is KycUploadStatus.GenericError -> {
                status.error?.let {
                    showErrorAlertIfNeeded(it)
                }
            }
            else -> {}
        }

        val state = getUploadDataForStatus(status)
        Timber.d("setUiState: $state")

        title!!.text = state.titleText
        subtitle!!.text = state.subtitleText
        submitButton!!.text = state.submitButtonLabel
        resultImageView!!.setImageResource(state.uploadResultImageName.getDrawableRes())
        resultImageView!!.visibility = if (state.isResultImageViewVisible) View.VISIBLE else View.GONE
        progressBar!!.visibility = if (state.isProgressBarVisible) View.VISIBLE else View.INVISIBLE
        submitButton!!.isEnabled = state.isSubmitButtonEnabled
        submitButton!!.setOnClickListener { state.submitButtonAction!!.invoke() }
    }

    private fun showErrorAlert(isFraud: Boolean) {
        if (isFraud) {
            showAlertFragment(
                    title = getString(R.string.failure_fraud_headline),
                    message = getString(R.string.failure_fraud_message),
                    positiveLabel = getString(R.string.failure_fraud_button),
                    tag = TAG_FAILURE_FRAUD,
                    positiveAction = { activityViewModel.resetFourthlineFlow() },
                    negativeAction = { activityViewModel.setFourthlineIdentificationFailure() }
            )
        } else {
            showAlertFragment(
                    title = getString(R.string.failure_no_fraud_headline),
                    message = getString(R.string.failure_no_fraud_message),
                    positiveLabel = getString(R.string.failure_no_fraud_positive),
                    negativeLabel = getString(R.string.failure_no_fraud_negative),
                    tag = TAG_FAILURE_NO_FRAUD,
                    positiveAction = { activityViewModel.resetFourthlineFlow() },
                    negativeAction = { activityViewModel.setFourthlineIdentificationFailure() }
            )
        }
    }

    private fun showErrorAlertIfNeeded(throwable: Throwable) {
        if (getErrorType(throwable) == Type.PreconditionFailed) {
            //TODO Handle 412 Here
        }
    }

    private fun String.getDrawableRes(): Int {
        return requireContext().resources.getIdentifier(this, "drawable", requireContext().packageName)
    }


    private fun moveToNextStep(nextStep: String) {
        Timber.d("moveToNextStep : ${nextStep}")
        activityViewModel.postDynamicNavigationNextStep(
                Bundle().apply { putString(NEXT_STEP_KEY, nextStep) }
        )
    }

    private fun startKycUploadService() {
        Timber.d("startKycUploadService")
        kycSharedViewModel.getKycUriZip(requireContext().applicationContext)?.let {
            val intent = Intent(requireActivity(), KycUploadService::class.java)
                    .apply {
                        putExtra(KYC_ZIPPER_URI, it)
                        putExtra(IdentHub.SESSION_URL_KEY, requireActivity().intent.getStringExtra(IdentHub.SESSION_URL_KEY))
                    }
            requireActivity().startService(intent)
        }?: kotlin.run {
            Timber.d("Can not obtain data for identification")
            Toast.makeText(
                    requireContext(),
                    "Can not obtain data for identification",
                    Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun bindKycUploadService() {
        requireActivity().bindService(
                Intent(requireActivity(), KycUploadService::class.java),
                serviceConnection,
                Service.BIND_ADJUST_WITH_ACTIVITY
        )
    }

    override fun onDestroyView() {
        binder?.uploadingStatus?.removeObserver(uploadingObserver)
        title = null
        subtitle = null
        progressBar = null
        resultImageView = null
        submitButton = null
        super.onDestroyView()
    }

    data class UploadViewState(
        val titleText: String,
        val subtitleText: String,
        val submitButtonLabel: String,
        val uploadResultImageName: String,
        val isResultImageViewVisible: Boolean,
        val isProgressBarVisible: Boolean,
        val submitButtonActionSendsResult: Boolean,
        val submitButtonActionResetsFlow: Boolean,
        val isSubmitButtonEnabled: Boolean,
        val submitButtonAction: (() -> Unit)?,
    )

    private fun getUploadDataForStatus(uploadStatus: KycUploadStatus): UploadViewState {
        return when (uploadStatus) {
            is KycUploadStatus.FinishIdentSuccess -> UploadViewState(
                    titleText = "Congratulation",
                    subtitleText = "Your data was confirmed",
                    submitButtonLabel = "Submit",
                    uploadResultImageName = "ic_upload_successful",
                    isResultImageViewVisible = true,
                    isProgressBarVisible = false,
                    submitButtonActionSendsResult = true,
                    submitButtonActionResetsFlow = false,
                    isSubmitButtonEnabled = true,
                    submitButtonAction = { activityViewModel.setFourthlineIdentificationSuccessful(uploadStatus.id) }
                )
            is KycUploadStatus.ToNextStepSuccess -> UploadViewState(
                    titleText = "Congratulation",
                    subtitleText = "Your data was confirmed",
                    submitButtonLabel = "Submit",
                    uploadResultImageName = "ic_upload_successful",
                    isResultImageViewVisible = true,
                    isProgressBarVisible = false,
                    submitButtonActionSendsResult = true,
                    submitButtonActionResetsFlow = false,
                    isSubmitButtonEnabled = true,
                    submitButtonAction = { moveToNextStep(uploadStatus.nextStep) }
            )
            is KycUploadStatus.Uploading -> UploadViewState(
                    titleText = "Verification",
                    subtitleText = "Please wait for verification",
                    submitButtonLabel = "Uploading...",
                    uploadResultImageName = "ic_upload_failed",
                    isResultImageViewVisible = false,
                    isProgressBarVisible = true,
                    submitButtonActionSendsResult = false,
                    submitButtonActionResetsFlow = false,
                    isSubmitButtonEnabled = false,
                    submitButtonAction = null
                )
            is KycUploadStatus.ProviderError -> UploadViewState(
                    titleText = "Please try again ...",
                    subtitleText = "Identification process failed",
                    submitButtonLabel = "Retry",
                    uploadResultImageName = "ic_upload_failed",
                    isResultImageViewVisible = true,
                    isProgressBarVisible = false,
                    submitButtonActionSendsResult = false,
                    submitButtonActionResetsFlow = true,
                    isSubmitButtonEnabled = true,
                    submitButtonAction =  { activityViewModel.resetFourthlineFlow() }
            )
            is KycUploadStatus.GenericError -> UploadViewState(
                    titleText = "Please try again ...",
                    subtitleText = "Identification process failed",
                    submitButtonLabel = "Retry",
                    uploadResultImageName = "ic_upload_failed",
                    isResultImageViewVisible = true,
                    isProgressBarVisible = false,
                    submitButtonActionSendsResult = false,
                    submitButtonActionResetsFlow = true,
                    isSubmitButtonEnabled = true,
                    submitButtonAction =  { activityViewModel.setFourthlineIdentificationFailure() }
            )
        }
    }

    companion object {
        const val TAG_FAILURE_FRAUD = "FailureFraud"
        const val TAG_FAILURE_NO_FRAUD = "FailureNoFraud"
    }
}