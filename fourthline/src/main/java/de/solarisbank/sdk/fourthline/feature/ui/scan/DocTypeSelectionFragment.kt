package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.fourthline.feature.ui.scan.DocScanFragment.Companion.DOC_TYPE_KEY

class DocTypeSelectionFragment: FourthlineFragment() {

    private var pasRB: RadioButton? = null
    private var idRB: RadioButton? = null
    private var confirmButton: Button? = null
    private var passportTitle: TextView? = null
    private var idTitle: TextView? = null
    private var pasWrapperCardView: CardView? = null
    private var idWrapperCardView: CardView? = null

    private val clickListener = View.OnClickListener { view ->
        when (view) {
            pasWrapperCardView -> {
                pasRB!!.isChecked = true
                idRB!!.post { idRB!!.isChecked = false }
                confirmButton!!.isEnabled = true
                kycSharedViewModel.type = DocScanFragment.TYPE_PASSPORT
            }
            idWrapperCardView -> {
                idRB!!.isChecked = true
                pasRB!!.post { pasRB!!.isChecked = false }
                confirmButton!!.isEnabled = true
                kycSharedViewModel.type = DocScanFragment.TYPE_ID
            }
        }
    }

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

            val typePassportViewGroup: ViewGroup = it.findViewById(R.id.passportType)
            val typeIdViewGroup: ViewGroup = it.findViewById(R.id.idType)

            pasWrapperCardView = typePassportViewGroup.findViewById(R.id.cardWrapper)
            idWrapperCardView = typeIdViewGroup.findViewById(R.id.cardWrapper)

            pasRB = typePassportViewGroup.findViewById(R.id.typeRadiobutton)
            idRB = typeIdViewGroup.findViewById(R.id.typeRadiobutton)

            passportTitle = typePassportViewGroup.findViewById(R.id.idTypeTV)
            idTitle = typeIdViewGroup.findViewById(R.id.idTypeTV)

            confirmButton = it.findViewById(R.id.confirmButton)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        kycSharedViewModel.documentTypesLiveData.observe(viewLifecycleOwner, { appearAvailableDocTypes(it) })
        kycSharedViewModel.fetchPersonData(requireActivity().intent)
    }

    override fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, arguments)
    }

    private fun initView() {
        passportTitle!!.text = "Passport"
        idTitle!!.text = "ID"
        pasWrapperCardView!!.setOnClickListener(clickListener)
        idWrapperCardView!!.setOnClickListener(clickListener)
        confirmButton!!.setOnClickListener {
            if (pasRB!!.isChecked && !idRB!!.isChecked) {
                moveToDocScanFragment(DocScanFragment.Companion.TYPE_PASSPORT)
            } else if(idRB!!.isChecked && !pasRB!!.isChecked) {
                moveToDocScanFragment(DocScanFragment.Companion.TYPE_ID)
            }
        }

        kycSharedViewModel.type?.let {
            when (it) {
                DocScanFragment.TYPE_PASSPORT -> {
                    pasRB!!.isEnabled = true
                    idRB!!.isEnabled = false
                }
                DocScanFragment.TYPE_ID -> {
                    idRB!!.isEnabled = true
                    pasRB!!.isEnabled = false
                }
            }
        }
    }

    private fun appearAvailableDocTypes(docs: List<AppliedDocument>){
        docs.forEach {
            when (it) {
                AppliedDocument.PASSPORT -> pasWrapperCardView!!.visibility = View.VISIBLE
                AppliedDocument.ID_CARD -> idWrapperCardView!!.visibility = View.VISIBLE
            }
        }
    }

    private fun moveToDocScanFragment(docType: Int) {
        activityViewModel.navigateToDocScanFragment(Bundle().apply { putInt(DOC_TYPE_KEY, docType) })
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
        pasRB = null
        idRB = null
        confirmButton = null
        passportTitle = null
        idTitle = null
        pasWrapperCardView = null
        idWrapperCardView = null
        super.onDestroyView()
    }
}
