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
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.extension.buttonDisabled
import de.solarisbank.sdk.fourthline.FourthlineModule
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.data.dto.AppliedDocument
import de.solarisbank.sdk.fourthline.domain.dto.PersonDataStateDto
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.FOURTHLINE_SCAN_FAILED
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.KEY_CODE
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.KEY_MESSAGE
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.logger.IdLogger
import org.koin.androidx.navigation.koinNavGraphViewModel
import timber.log.Timber

class DocTypeSelectionFragment: BaseFragment() {

    private var docTypeAdapter: DocTypeAdapter? = null
    private var documentTypeList: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var confirmButton: Button? = null

    private val kycSharedViewModel: KycSharedViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)
    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)

    private val requestPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
                permissions ->
            permissions.forEach { (permission, isGranted) ->
                if (!isGranted) {
                    showRationale(permission)
                    return@registerForActivityResult
                }
            }
            fetchData()
        }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Timber.d("onCreateView")
        return inflater.inflate(R.layout.identhub_fragment_doc_type_selection, container, false).also {
            documentTypeList = it.findViewById(R.id.documentTypeList)
            confirmButton = it.findViewById<Button?>(R.id.confirmButton).apply { buttonDisabled(true) }
            progressBar = it.findViewById(R.id.progress)
        }
    }

    override fun customizeView(view: View) {
        confirmButton?.customize(customization)
        progressBar?.customize(customization)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        handleErrors(savedInstanceState)
        kycSharedViewModel.supportedDocLiveData.observe(viewLifecycleOwner) { processState(it) }
    }

    private fun initRecyclerView() {
        documentTypeList!!.layoutManager = LinearLayoutManager(requireContext())
        documentTypeList!!.setHasFixedSize(true)
        docTypeAdapter = DocTypeAdapter(customization) { setConfirmButtonEnabled() }
        documentTypeList!!.adapter = docTypeAdapter
        confirmButton!!.setOnClickListener { moveToDocScanFragment() }
    }

    private fun setConfirmButtonEnabled(): Boolean {
        confirmButton!!.buttonDisabled(false)
        return true
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
                        positiveAction = { identificationFailed(
                            "Id document was not supported and we aborted the flow"
                        ) },
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
                        identificationFailed(
                            "We could not obtain the location and the user choose to abort"
                        )
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
                        identificationFailed(
                            "User did not enable their location and chose to abort"
                        )
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
                            identificationFailed(
                                "Unknown error happened doc type selection screen"
                            )
                        }
                )
            }
            else -> { /* Ignore */ }
        }
    }

    private fun moveToDocScanFragment() {
        activityViewModel.onDocTypeSelectionOutcome(
            DocTypeSelectionOutcome.Success(docTypeAdapter!!.getSelectedDocType())
        )
    }

    private fun identificationFailed(message: String) {
        activityViewModel.onDocTypeSelectionOutcome(DocTypeSelectionOutcome.Failed(message))
    }

    override fun onStart() {
        super.onStart()
        proceedWithPermissions()
    }

    private fun proceedWithPermissions() {
        requestPermission.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )
    }

    private fun showRationale(permission: String) {
        showAlertFragment(
            title = getString(R.string.identhub_fourthline_permission_rationale_title),
            message = getString(R.string.identhub_fourthline_permission_rationale_message),
            negativeLabel = getString(R.string.identhub_fourthline_permission_rationale_ok),
            positiveLabel = getString(R.string.identhub_fourthline_permission_rationale_quit),
            negativeAction = {
                if (!shouldShowRequestPermissionRationale(permission)) {
                    Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", requireContext().packageName, null)
                    }.also {
                        IdLogger.warn("Going for settings for permission: $permission")
                        startActivity(it)
                    }
                } else {
                    IdLogger.warn("Requesting permission again: $permission")
                    proceedWithPermissions()
                }
            },
            positiveAction = {
                IdLogger.error("The user did not give the permission: $permission")
                identificationFailed("User did not grant the required permissions")
            }
        )
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
        val code = arguments?.getString(KEY_CODE)
        val message = arguments?.getString(KEY_MESSAGE)
        if (saved != null) {
            return
        }
        if (code == FOURTHLINE_SCAN_FAILED) {
            showAlertFragment(
                getString(R.string.identhub_scanner_error_title),
                message ?: getString(R.string.identhub_scanner_error_unknown),
                getString(R.string.identhub_scanner_error_scan_button_quit),
                getString(R.string.identhub_scanner_error_scan_button_retry),
                positiveAction = { identificationFailed(
                        "User could not take a scan of their document and aborted the flow"
                )},
                negativeAction = {},
                tag = "DocScanError"
            )
        }
    }
}

sealed class DocTypeSelectionOutcome {
    data class Success(val docType: AppliedDocument): DocTypeSelectionOutcome()
    data class Failed(val message: String): DocTypeSelectionOutcome()
}