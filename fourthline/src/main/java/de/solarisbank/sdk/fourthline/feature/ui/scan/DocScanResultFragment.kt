package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.custom.DateInputEditText
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.fourthline.getDateFromMRZ
import timber.log.Timber


class DocScanResultFragment : FourthlineFragment() {

    private val activityViewModel: FourthlineViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)
                .get(FourthlineViewModel::class.java)
    }

    private val kycSharedViewModel: KycSharedViewModel by lazy<KycSharedViewModel> {
        ViewModelProvider(requireActivity(), (requireActivity() as FourthlineActivity).viewModelFactory)[KycSharedViewModel::class.java]
    }

    private var title: TextView? = null
    private var docNumberInputLayout: TextInputLayout? = null
    private var docNumberTextInput: TextInputEditText? = null
    private var issueDateInputLayout: TextInputLayout? = null
    private var issueDateTextInput: DateInputEditText? = null
    private var expireDateInputLayout: TextInputLayout? = null
    private var expireDateTextInput: DateInputEditText? = null
    private var continueButton: Button? = null

    private val textValidationWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            continueButton!!.isEnabled = issueDateTextInput?.getDate() != null
                    && expireDateTextInput?.getDate() != null
                    && !issueDateTextInput!!.text.isNullOrEmpty()
                    && !expireDateTextInput!!.text.isNullOrEmpty()
                    && !docNumberTextInput!!.text.isNullOrEmpty()
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
                    docNumberInputLayout = it.findViewById(R.id.docNumberInputLayout)
                    docNumberTextInput = it.findViewById(R.id.docNumberTextInput)
                    issueDateInputLayout = it.findViewById(R.id.issueDateInputLayout)
                    issueDateTextInput = it.findViewById(R.id.issueDateTextInput)
                    expireDateInputLayout = it.findViewById(R.id.expireDateInputLayout)
                    expireDateTextInput = it.findViewById(R.id.expireDateTextInput)
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
            docNumberTextInput!!.text = Editable.Factory.getInstance().newEditable(doc.number)
            doc.issueDate?.let { issueDateTextInput!!.setDate(it) }?:let {
                doc.expirationDate?.let {
                    Timber.d("count issue date")
                    issueDateTextInput!!.countIssueDate(it)
                }
            }
            doc.expirationDate?.let { expireDateTextInput!!.setDate(it) }
        }

        continueButton!!.setOnClickListener {
            val doc = kycSharedViewModel.getKycDocument()
            doc.issueDate = issueDateTextInput!!.text.toString().getDateFromMRZ()
            doc.number = docNumberTextInput!!.text.toString()
            doc.expirationDate = expireDateTextInput!!.text.toString().getDateFromMRZ()
            activityViewModel.navigateToLocationAccessFragment()
        }
    }

    override fun onDestroyView() {
        issueDateTextInput?.removeTextChangedListener(textValidationWatcher)
        expireDateTextInput?.removeTextChangedListener(textValidationWatcher)
        docNumberTextInput?.removeTextChangedListener(textValidationWatcher)
        title = null
        docNumberInputLayout = null
        docNumberTextInput = null
        issueDateInputLayout = null
        issueDateTextInput = null
        expireDateInputLayout = null
        expireDateTextInput = null
        continueButton = null
        super.onDestroyView()
    }
}

