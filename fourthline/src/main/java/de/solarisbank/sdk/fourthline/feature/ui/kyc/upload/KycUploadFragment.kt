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
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.identhub.session.IdentHub
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
            bound = false
        }

    }

    private val uploadingObserver = Observer<KycUploadServiceBinder.Companion.UPLOADING_STATUS> {
        setUiState(it)
    }

    private var title: TextView? = null
    private var subtitle:  TextView? = null
    private var progressBar: ProgressBar? = null
    private var quitButton: Button? = null

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
                    quitButton = it.findViewById(R.id.quitButton)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startKycUploadService()
        bindKycUploadService()
    }

    private fun setUiState(state: KycUploadServiceBinder.Companion.UPLOADING_STATUS) {
        when (state) {
            KycUploadServiceBinder.Companion.UPLOADING_STATUS.UPLOADING -> progressBar!!.visibility = View.VISIBLE
            KycUploadServiceBinder.Companion.UPLOADING_STATUS.DONE -> {
                //todo add call to activity's step indicators
                progressBar!!.visibility = View.INVISIBLE
            }
            KycUploadServiceBinder.Companion.UPLOADING_STATUS.ERROR -> {
                //todo add call to activity's step indicator
                progressBar!!.visibility = View.INVISIBLE
            }
        }
    }

    private fun startKycUploadService() {
        progressBar!!.visibility = View.VISIBLE
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
        title = null
        subtitle = null
        progressBar = null
        quitButton = null
        super.onDestroyView()
    }

}