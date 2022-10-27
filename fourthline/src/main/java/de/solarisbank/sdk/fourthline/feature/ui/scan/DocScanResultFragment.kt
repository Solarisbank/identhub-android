package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import de.solarisbank.identhub.session.main.NewBaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.view.hideKeyboard
import de.solarisbank.sdk.fourthline.FourthlineModule
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.custom.DateInputTextView
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.logger.IdLogger
import org.koin.androidx.navigation.koinNavGraphViewModel
import timber.log.Timber
import java.util.*


class DocScanResultFragment : NewBaseFragment() {

    private val kycSharedViewModel: KycSharedViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)
    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)

    private var title: TextView? = null
    private var docNumberTextInput: EditText? = null
    private var issueDateTextInput: DateInputTextView? = null
    private var issueDateError: TextView? = null
    private var expireDateTextInput: DateInputTextView? = null
    private var expiryDateError: TextView? = null
    private var continueButton: Button? = null
    private var issueDateWatcher: TextWatcher? = null
    private var expireDateWatcher: TextWatcher? = null
    private var docNumberWatcher: TextWatcher? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.identhub_fragment_doc_scan_result, container, false)
            .also {
                title = it.findViewById(R.id.title)
                docNumberTextInput = it.findViewById(R.id.docNumberTextInput)
                issueDateTextInput = it.findViewById(R.id.issueDateTextInput)
                issueDateError = it.findViewById(R.id.issueDateError)
                expireDateTextInput = it.findViewById(R.id.expireDateTextInput)
                expiryDateError = it.findViewById(R.id.expireDateError)
                continueButton = it.findViewById(R.id.continueButton)
                customizeUI()
            }
    }

    private fun customizeUI() {
        continueButton?.customize(customization)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        issueDateWatcher = issueDateTextInput?.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            issueDateTextInput?.getDate()?.let {
                expireDateTextInput?.setSuggestedDateWithOffset(it, DATE_OFFSET_YEARS)
            }
            updateContinueButtonState()
        })
        expireDateWatcher = expireDateTextInput?.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            expireDateTextInput?.getDate()?.let {
                issueDateTextInput?.setSuggestedDateWithOffset(it, -DATE_OFFSET_YEARS)
            }
            updateContinueButtonState()
        })
        docNumberWatcher = docNumberTextInput?.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            updateContinueButtonState()
        })


        kycSharedViewModel.getKycDocument().let { doc ->
            IdLogger.info("Kyc Doc info was extracted: DocNumber: ${!doc.number.isNullOrEmpty()} " +
                    "ExpirationDate: ${doc.expirationDate != null}")
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
                hideKeyboard()
                activityViewModel.onDocResultOutcome()
            } else {
                showGenericAlertFragment {  }
            }
        }
    }

    override fun onDestroyView() {
        issueDateTextInput?.removeTextChangedListener(issueDateWatcher)
        expireDateTextInput?.removeTextChangedListener(expireDateWatcher)
        docNumberTextInput?.removeTextChangedListener(docNumberWatcher)
        title = null
        docNumberTextInput = null
        issueDateTextInput = null
        expireDateTextInput = null
        continueButton = null
        issueDateError = null
        expiryDateError = null
        super.onDestroyView()
    }

    private fun updateContinueButtonState() {
        continueButton?.isEnabled = validateDateInputs()
    }

    private fun validateDateInputs(): Boolean {
        val issueDate = issueDateTextInput?.getDate() ?: return false
        val expiryDate = expireDateTextInput?.getDate() ?: return false
        var valid = true
        if (issueDate > expiryDate) {
            issueDateError!!.text = getString(R.string.identhub_fourthline_doc_scan_date_past_error)
            issueDateError!!.visibility = View.VISIBLE
            valid = false
        } else if (issueDate > Date()) {
            issueDateError!!.text = getString(R.string.identhub_fourthline_doc_scan_issue_future_error)
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

    companion object {
        const val DATE_OFFSET_YEARS = 10
    }
}

