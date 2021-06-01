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
import de.solarisbank.identhub.router.NEXT_STEP_KEY
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.utils.isServiceRunning
import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.service.kyc.upload.KycUploadService
import de.solarisbank.sdk.fourthline.feature.service.kyc.upload.KycUploadService.Companion.KYC_ZIPPER_URI
import de.solarisbank.sdk.fourthline.feature.service.kyc.upload.KycUploadServiceBinder
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

    private val uploadingObserver = Observer<Pair<UPLOAD_STATE, String?>> {
        nextStep = it.second
        setUiState(it.first)
    }

    private var title: TextView? = null
    private var subtitle:  TextView? = null
    private var progressBar: ProgressBar? = null
    private var resultImageView: ImageView? = null
    private var submitButton: Button? = null

    private var bound: Boolean = false
    private var binder: KycUploadServiceBinder? = null
    private var nextStep: String? = null



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

    private fun setUiState(state: UPLOAD_STATE) {
        title!!.text = state.titleText
        subtitle!!.text = state.subtitleText
        submitButton!!.text = state.submitButtonLabel
        resultImageView!!.setImageResource(state.uploadResultImageName.getDrawableRes())
        resultImageView!!.visibility = if (state.isResultImageViewVisible) View.VISIBLE else View.GONE
        progressBar!!.visibility = if (state.isProgressBarVisible) View.VISIBLE else View.INVISIBLE
        if (state.submitButtonActionSendsResult) {
            submitButton!!.setOnClickListener { moveToNextStep() }
        } else if (state.submitButtonActionResetsFlow){
            submitButton!!.setOnClickListener { activityViewModel.resetFourthlineFlow() }
        } else {
            submitButton!!.setOnClickListener(null)
        }
        submitButton!!.isEnabled = state.isSubmitButtonEnabled
    }

    private fun String.getDrawableRes(): Int {
        return requireContext().resources.getIdentifier(this, "drawable", requireContext().packageName)
    }


    private fun moveToNextStep() {
        Timber.d("moveToNextStep : ${nextStep}")
        nextStep?.let { activityViewModel.postDynamicNavigationNextStep(Bundle().apply {
            putString(NEXT_STEP_KEY, nextStep)
        })}?:run { //todo check if it is needed
            kycSharedViewModel.sendCompletedResult(requireActivity())
        }
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

    interface UploadState {
        val titleText: String
        val subtitleText: String
        val submitButtonLabel: String
        val uploadResultImageName: String
        val isResultImageViewVisible: Boolean
        val isProgressBarVisible: Boolean
        val submitButtonActionSendsResult: Boolean
        val submitButtonActionResetsFlow: Boolean
        val isSubmitButtonEnabled: Boolean
    }

    enum class UPLOAD_STATE : UploadState {
        SUCCESSFUL {
            override val titleText = "Congratulation"
            override val subtitleText = "Your data was confirmed"
            override val submitButtonLabel: String = "Submit"
            override val uploadResultImageName = "ic_upload_successful"
            override val isResultImageViewVisible = true
            override val isProgressBarVisible = false
            override val submitButtonActionSendsResult = true
            override val submitButtonActionResetsFlow = false
            override val isSubmitButtonEnabled = true
        },
        FAIL {
            override val titleText = "Please try again ..."
            override val subtitleText = "Identification process failed"
            override val submitButtonLabel: String = "Retry"
            override val uploadResultImageName = "ic_upload_failed"
            override val isResultImageViewVisible = true
            override val isProgressBarVisible = false
            override val submitButtonActionSendsResult = false
            override val submitButtonActionResetsFlow = true
            override val isSubmitButtonEnabled = true
        },
        UPLOADING {
            override val titleText = "Verification"
            override val subtitleText = "Please wait for verification"
            override val submitButtonLabel: String = "Uploading ..."
            override val uploadResultImageName = "ic_upload_failed"
            override val isResultImageViewVisible = false
            override val isProgressBarVisible = true
            override val submitButtonActionSendsResult = false
            override val submitButtonActionResetsFlow = false
            override val isSubmitButtonEnabled = false
        }
    }
}