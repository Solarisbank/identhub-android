package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.sdk.core.activityViewModels
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

    private val kycSharedViewModel: KycSharedViewModel by lazy<KycSharedViewModel> {
        activityViewModels()
    }

    private val activityViewModel: FourthlineViewModel by lazy<FourthlineViewModel> {
        activityViewModels()
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("onCreateView")
        return inflater.inflate(R.layout.fragment_doc_type_selection, container, false).also {
            documentTypeList = it.findViewById(R.id.documentTypeList)
            confirmButton = it.findViewById(R.id.confirmButton)
            progressBar = it.findViewById(R.id.progress)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initViewModel()
        handleErrors(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        kycSharedViewModel.supportedDocLiveData.observe(viewLifecycleOwner, { processState(it) })
    }

    override fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, arguments)
    }

    private fun initRecyclerView() {
        documentTypeList!!.layoutManager = LinearLayoutManager(requireContext())
        documentTypeList!!.setHasFixedSize(true)
        docTypeAdapter = DocTypeAdapter { type -> confirmButton!!.isEnabled = (type != null) }
        documentTypeList!!.adapter = docTypeAdapter
        confirmButton!!.setOnClickListener { moveToDocScanFragment() }
    }

    private fun processState(personDataStateDto: PersonDataStateDto) {
        Timber.d("processState 0 : ${personDataStateDto}")
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
                        getString(R.string.fourthline_doc_type_country_not_supported_headline),
                        getString(R.string.fourthline_doc_type_country_not_supported_message),
                        getString(R.string.fourthline_doc_type_country_not_supported_button),
                        positiveAction = { activityViewModel.setFourthlineIdentificationFailure() },
                        tag = "DocScanError"
                )
            }
            is PersonDataStateDto.LOCATION_FETCHING_ERROR, is PersonDataStateDto.NETWORK_NOT_ENABLED_ERROR -> {
                Timber.d("processState 3")
                progressBar!!.visibility = View.INVISIBLE
                showAlertFragment(
                    //todo add trasnlation
                    title = getString(R.string.location_fetching_error_title),
                    message = getString(R.string.location_fetching_error_message),
                    positiveLabel = getString(R.string.retry_button),
                    negativeLabel = getString(R.string.quit_location_button),
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
                    //todo add trasnlation
                    title = getString(R.string.location_not_active_title),
                    message = getString(R.string.location_not_active_message),
                    positiveLabel = getString(R.string.enable_location_button),
                    negativeLabel = getString(R.string.quit_location_button),
                    positiveAction = {
                        requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        Handler(Looper.getMainLooper()).postDelayed({
                            processState(PersonDataStateDto.RETRY_LOCATION_FETCHING)
                        }, 1000)
                    },
                    negativeAction = {
                        activityViewModel.setFourthlineIdentificationFailure()
                    }

                )
            }
            is PersonDataStateDto.GENERIC_ERROR -> {
                Timber.d("processState 4")
                progressBar!!.visibility = View.INVISIBLE
                showAlertFragment(
                        title = getString(R.string.generic_error_title),
                        message = getString(R.string.generic_error_message),
                        positiveLabel = getString(R.string.quit_location_button),
                        positiveAction = {
                            activityViewModel.setFourthlineIdentificationFailure()
                        }
                )
            }
        }
    }

    private fun moveToDocScanFragment() {
        activityViewModel.navigateToDocScanFragment(Bundle()
                .apply { putSerializable(
                            DocScanFragment.DOC_TYPE_KEY,
                            docTypeAdapter!!.getSelectedDocType()
                ) })
    }

    override fun onResume() {
        super.onResume()
        requestLocationPermission()
    }


    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            Timber.d("requestLocationPermission() 1")
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        } else {
            Timber.d("requestLocationPermission() 2")
            fetchData()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Timber.d("onRequestPermissionsResult")
        if (requestCode == LOCATION_PERMISSION_CODE && (grantResults.isEmpty() || grantResults[0] != PermissionChecker.PERMISSION_GRANTED)) {
            Timber.d("onRequestPermissionsResult 1")
            requestLocationPermission()
        } else if (requestCode == LOCATION_PERMISSION_CODE && (grantResults.isNotEmpty() && grantResults[0] == PermissionChecker.PERMISSION_GRANTED)){
            Timber.d("onRequestPermissionsResult 2")
            fetchData()
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
                getString(R.string.scanner_error_title),
                message ?: getString(R.string.scanner_error_unknown),
                getString(R.string.scanner_error_scan_button_retry),
                getString(R.string.scanner_error_scan_button_quit),
                positiveAction = {},
                negativeAction = { activityViewModel.setFourthlineIdentificationFailure() },
                tag = "DocScanError"
            )
        }
    }

    companion object {
        const val LOCATION_PERMISSION_CODE = 42
    }
}