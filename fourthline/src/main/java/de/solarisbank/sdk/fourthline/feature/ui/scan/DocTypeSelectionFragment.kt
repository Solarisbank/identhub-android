package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.alert.AlertDialogFragment
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.fourthline.feature.ui.scan.DocScanFragment.Companion.DOC_TYPE_KEY

class DocTypeSelectionFragment: FourthlineFragment() {

    private lateinit var docTypeAdapter: DocTypeAdapter
    private lateinit var documentTypeList: RecyclerView
    private lateinit var progressBar: ProgressBar

    private var confirmButton: TextView? = null

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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        kycSharedViewModel.documentTypesLiveData.observe(viewLifecycleOwner, { appearAvailableDocTypes(it) })
        kycSharedViewModel.fetchPersonData(requireActivity().intent)
        alertViewModel.events.observe(viewLifecycleOwner) {
            it?.let {
                activityViewModel.setFourthlineIdentificationFailure()
            }
        }
    }

    override fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, arguments)
    }

    private fun initRecyclerView() {
        documentTypeList.layoutManager = LinearLayoutManager(requireContext())
        documentTypeList.setHasFixedSize(true)
        docTypeAdapter = DocTypeAdapter { type -> confirmButton!!.isEnabled = (type != null) }
        documentTypeList.adapter = docTypeAdapter
        confirmButton!!.setOnClickListener { moveToDocScanFragment() }
    }

    private fun appearAvailableDocTypes(docs: List<AppliedDocument>){
        progressBar.visibility = View.INVISIBLE
        val supportedDocs = docs.filter { it.isSupported }
        if (supportedDocs.isEmpty()) {
            fragmentManager?.let {
                AlertDialogFragment.newInstance(
                    getString(R.string.fourthline_doc_type_country_not_supported_headline),
                    getString(R.string.fourthline_doc_type_country_not_supported_message),
                    getString(R.string.fourthline_doc_type_country_not_supported_button)
                ).show(it, "DocumentNotSupported")
            }
        }
        docTypeAdapter.add(supportedDocs)
        docTypeAdapter.notifyDataSetChanged()
    }

    private fun moveToDocScanFragment() {
        activityViewModel.navigateToDocScanFragment(Bundle().apply { putSerializable(DOC_TYPE_KEY, docTypeAdapter.getSelectedDocType()) })
    }

    override fun onResume() {
        super.onResume()
        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 32)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 32 && (grantResults.isEmpty() || grantResults[0] != PermissionChecker.PERMISSION_GRANTED)) {
            Toast.makeText(requireContext(), "Camera permission is required to proceed", Toast.LENGTH_SHORT)
                .show()
            requestCameraPermission()
        }
    }

    override fun onDestroyView() {
        confirmButton = null
        super.onDestroyView()
    }
}