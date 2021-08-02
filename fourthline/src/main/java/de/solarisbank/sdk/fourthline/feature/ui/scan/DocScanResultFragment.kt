package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.domain.dto.ZipCreationStateDto
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.custom.DateInputEditText
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import timber.log.Timber
import java.util.*


class DocScanResultFragment : FourthlineFragment() {

    private val activityViewModel: FourthlineViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)
                .get(FourthlineViewModel::class.java)
    }

    private val kycSharedViewModel: KycSharedViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as FourthlineActivity).viewModelFactory)[KycSharedViewModel::class.java]
    }

    private var title: TextView? = null
    private var docNumberTextInput: EditText? = null
    private var issueDateTextInput: DateInputEditText? = null
    private var issueDateError: TextView? = null
    private var expireDateTextInput: DateInputEditText? = null
    private var expiryDateError: TextView? = null
    private var continueButton: Button? = null

    private val textValidationWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            continueButton!!.isEnabled = validateDateInputs()
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_doc_scan_result, container, false)
                .also {
                    title = it.findViewById(R.id.title)
                    docNumberTextInput = it.findViewById(R.id.docNumberTextInput)
                    issueDateTextInput = it.findViewById(R.id.issueDateTextInput)
                    issueDateError = it.findViewById(R.id.issueDateError)
                    expireDateTextInput = it.findViewById(R.id.expireDateTextInput)
                    expiryDateError = it.findViewById(R.id.expireDateError)
                    continueButton = it.findViewById(R.id.continueButton)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        issueDateTextInput!!.addTextChangedListener(textValidationWatcher)
        expireDateTextInput!!.addTextChangedListener(textValidationWatcher)
        docNumberTextInput!!.addTextChangedListener(textValidationWatcher)

        kycSharedViewModel.getKycDocument().let { doc ->
            docNumberTextInput!!.setText(doc.number)
            doc.issueDate?.let { issueDateTextInput!!.setDate(it) }?:let {
                doc.expirationDate?.let {
                    Timber.d("count issue date")
                    issueDateTextInput!!.countIssueDate(it)
                }
            }
            doc.expirationDate?.let { expireDateTextInput!!.setDate(it) }
        }

        continueButton!!.setOnClickListener {
            val issueDate = issueDateTextInput!!.getDate()
            val expirationDate = expireDateTextInput!!.getDate()
            val number = docNumberTextInput!!.text.toString().filter { !it.isWhitespace() }
            Timber.d("KycDocument data set" +
                    "\nissueDate: $issueDate" +
                    "\nexpirationDate $expirationDate" +
                    "\nnumber $number")

            if (issueDate != null && expirationDate != null && !number.isNullOrBlank()) {
                kycSharedViewModel.updateIssueDate(issueDate)
                kycSharedViewModel.updateExpireDate(expirationDate)
                kycSharedViewModel.updateDocumentNumber(number)
                val kycCreationState = kycSharedViewModel.createKycZip(requireContext().applicationContext)
                if (kycCreationState is ZipCreationStateDto.SUCCESS) {
                    kycSharedViewModel.kycURI = kycCreationState.uri
                    activityViewModel.navigateToKycUploadFragemnt()
                } else {
                    showGenericAlertFragment {  }
                }
            } else {
                showGenericAlertFragment {  }
            }
        }
    }

    override fun onDestroyView() {
        issueDateTextInput?.removeTextChangedListener(textValidationWatcher)
        expireDateTextInput?.removeTextChangedListener(textValidationWatcher)
        docNumberTextInput?.removeTextChangedListener(textValidationWatcher)
        title = null
        docNumberTextInput = null
        issueDateTextInput = null
        expireDateTextInput = null
        continueButton = null
        super.onDestroyView()
    }

    private fun validateDateInputs(): Boolean {
        val issueDate = issueDateTextInput?.getDate() ?: return false
        val expiryDate = expireDateTextInput?.getDate() ?: return false
        var valid = true
        if (issueDate > expiryDate) {
            issueDateError!!.setText(getString(R.string.fourthline_doc_scan_date_past_error))
            issueDateError!!.visibility = View.VISIBLE
            valid = false
        } else if (issueDate > Date()) {
            issueDateError!!.setText(getString(R.string.fourthline_doc_scan_issue_future_error))
            issueDateError!!.visibility = View.VISIBLE
            valid = false
        } else {
            issueDateError!!.visibility = View.INVISIBLE
        }
        if (expiryDate <= Date()) {
            expiryDateError!!.visibility = View.VISIBLE
            valid = false
        } else {
            expiryDateError!!.visibility = View.INVISIBLE
        }
        return !issueDateTextInput!!.text.isNullOrEmpty()
                && !expireDateTextInput!!.text.isNullOrEmpty()
                && !docNumberTextInput!!.text.isNullOrEmpty()
                && valid
    }
}

