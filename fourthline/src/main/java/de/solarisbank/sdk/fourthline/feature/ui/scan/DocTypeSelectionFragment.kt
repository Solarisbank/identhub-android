package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.domain.dto.PersonDataStateDto
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.KEY_MESSAGE
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import timber.log.Timber

class DocTypeSelectionFragment: FourthlineFragment() {

    private var docTypeAdapter: DocTypeAdapter? = null
    private var documentTypeList: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var confirmButton: Button? = null
    private var imageView: ImageView? = null

    private val kycSharedViewModel: KycSharedViewModel by lazy { activityViewModels() }

    private val activityViewModel: FourthlineViewModel by lazy { activityViewModels() }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("onCreateView")
        return inflater.inflate(R.layout.identhub_fragment_doc_type_selection, container, false).also {
            documentTypeList = it.findViewById(R.id.documentTypeList)
            confirmButton = it.findViewById(R.id.confirmButton)
            progressBar = it.findViewById(R.id.progress)
            imageView = it.findViewById(R.id.scratch)
            customizeUI()
        }
    }

    private fun customizeUI() {
        imageView?.isVisible = customization.customFlags.shouldShowLargeImages
        confirmButton?.customize(customization)
        progressBar?.customize(customization)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initViewModel()
        handleErrors(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        kycSharedViewModel.supportedDocLiveData.observe(viewLifecycleOwner) { processState(it) }
    }

    override fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, arguments)
    }

    private fun initRecyclerView() {
        documentTypeList!!.layoutManager = LinearLayoutManager(requireContext())
        documentTypeList!!.setHasFixedSize(true)
        docTypeAdapter = DocTypeAdapter(customization) { type -> confirmButton!!.isEnabled = (type != null) }
        documentTypeList!!.adapter = docTypeAdapter
        confirmButton!!.setOnClickListener { moveToDocScanFragment() }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun processState(personDataStateDto: PersonDataStateDto) {
        Timber.d("processState 0 : $personDataStateDto")
        when (personDataStateDto) {
            is PersonDataStateDto.UPLOADING -> {
                Timber.d("processState 1")
                progressBar!!.visibility = View.VISIBLE
            }
            is PersonDataStateDto.SUCCEEDED -> {
                Timber.d("processState 2, docs : ${personDataStateDto.docs}")
                progressBar!!.visibility = View.INVISIBLE
                docTypeAdapter!!.add(personDataStateDto.docs)
                docTypeAdapter!!.notifyDataSetChanged()
            }
            is PersonDataStateDto.EMPTY_DOCS_LIST_ERROR -> {
                Timber.d("processState 3")
                progressBar!!.visibility = View.INVISIBLE
                showAlertFragment(
                        getString(R.string.identhub_fourthline_doc_type_country_not_supported_headline),
                        getString(R.string.identhub_fourthline_doc_type_country_not_supported_message),
                        getString(R.string.identhub_fourthline_doc_type_country_not_supported_button),
                        positiveAction = { activityViewModel.setFourthlineIdentificationFailure() },
                        tag = "DocScanError"
                )
            }
            is PersonDataStateDto.LOCATION_FETCHING_ERROR, is PersonDataStateDto.NETWORK_NOT_ENABLED_ERROR -> {
                Timber.d("processState 3")
                progressBar!!.visibility = View.INVISIBLE
                showAlertFragment(
                    //todo add trasnlation
                    title = getString(R.string.identhub_location_fetching_error_title),
                    message = getString(R.string.identhub_location_fetching_error_message),
                    positiveLabel = getString(R.string.identhub_doc_type_retry_button),
                    negativeLabel = getString(R.string.identhub_quit_location_button),
                    positiveAction = {
                        fetchData()
                    },
                    negativeAction = {
                        activityViewModel.setFourthlineIdentificationFailure()
                    }

                )
            }
            is PersonDataStateDto.LOCATION_CLIENT_NOT_ENABLED_ERROR -> {
                showAlertFragment(
                    title = getString(R.string.identhub_location_not_active_title),
                    message = getString(R.string.identhub_location_not_active_message),
                    negativeLabel = getString(R.string.identhub_enable_location_button),
                    positiveLabel = getString(R.string.identhub_quit_location_button),
                    negativeAction = {
                        requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        Handler(Looper.getMainLooper()).postDelayed({
                            processState(PersonDataStateDto.RETRY_LOCATION_FETCHING)
                        }, 1000)
                    },
                    positiveAction = {
                        activityViewModel.setFourthlineIdentificationFailure()
                    }

                )
            }
            is PersonDataStateDto.GENERIC_ERROR -> {
                Timber.d("processState 4")
                progressBar!!.visibility = View.INVISIBLE
                showAlertFragment(
                        title = getString(R.string.identhub_generic_error_title),
                        message = getString(R.string.identhub_generic_error_message),
                        positiveLabel = getString(R.string.identhub_quit_location_button),
                        positiveAction = {
                            activityViewModel.setFourthlineIdentificationFailure()
                        }
                )
            }
        }
    }

    private fun moveToDocScanFragment() {
        activityViewModel.navigateFromDocTypeSelectionToDocScan(Bundle()
                .apply { putSerializable(
                            DocScanFragment.DOC_TYPE_KEY,
                            docTypeAdapter!!.getSelectedDocType()
                ) })
    }

    override fun onStart() {
        super.onStart()
        requestLocationPermission(askedBefore = false, rationalize = false)
    }

    private fun requestLocationPermission(askedBefore: Boolean = false, rationalize: Boolean = true) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PermissionChecker.PERMISSION_GRANTED) {
            Timber.d("requestLocationPermission() 1")
            if (shouldShowRequestPermissionRationale(permission)) {
                if (rationalize) {
                    showLocationPermissionRationale(alwaysDenied = false)
                } else {
                    requestPermissions(arrayOf(permission), LOCATION_PERMISSION_CODE)
                }
            } else if (askedBefore) {
                showLocationPermissionRationale(alwaysDenied = true)
            } else {
                requestPermissions(arrayOf(permission), LOCATION_PERMISSION_CODE)
            }
        } else {
            Timber.d("requestLocationPermission() 2")
            fetchData()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Timber.d("onRequestPermissionsResult")
        if (requestCode == LOCATION_PERMISSION_CODE) {
            requestLocationPermission(askedBefore = true, rationalize = true)
        }
    }

    private fun fetchData() {
        Timber.d("fetchData()")
        kycSharedViewModel.fetchPersonDataAndLocation()
    }

    override fun onDestroyView() {
        confirmButton = null
        docTypeAdapter = null
        documentTypeList = null
        progressBar = null
        imageView = null
        super.onDestroyView()
    }

    private fun handleErrors(saved: Bundle?) {
        val code = arguments?.getString(FourthlineActivity.KEY_CODE)
        val message = arguments?.getString(KEY_MESSAGE)
        if (saved != null) {
            return
        }
        if (code == FourthlineActivity.FOURTHLINE_SCAN_FAILED) {
            showAlertFragment(
                getString(R.string.identhub_scanner_error_title),
                message ?: getString(R.string.identhub_scanner_error_unknown),
                getString(R.string.identhub_scanner_error_scan_button_retry),
                getString(R.string.identhub_scanner_error_scan_button_quit),
                positiveAction = {},
                negativeAction = { activityViewModel.setFourthlineIdentificationFailure() },
                tag = "DocScanError"
            )
        }
    }

    private fun showLocationPermissionRationale(alwaysDenied: Boolean) {
        showAlertFragment(
            title = getString(R.string.identhub_fourthline_permission_rationale_title),
            message = getString(R.string.identhub_fourthline_location_permission_rationale_message),
            negativeLabel = getString(R.string.identhub_fourthline_permission_rationale_ok),
            negativeAction = {
                if (alwaysDenied) {
                    Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", requireContext().packageName, null)
                    }.also {
                        startActivity(it)
                    }
                } else {
                    requestLocationPermission(askedBefore = true, rationalize = false)
                }
            },
            positiveLabel = getString(R.string.identhub_fourthline_permission_rationale_quit),
            positiveAction = {
                activityViewModel.setFourthlineIdentificationFailure()
            }
        )
    }

    companion object {
        const val LOCATION_PERMISSION_CODE = 42
    }
}