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
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.extension.buttonDisabled
import de.solarisbank.sdk.feature.view.hideKeyboard
import de.solarisbank.sdk.fourthline.FourthlineFlow
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.custom.DateInputTextView
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.logger.IdLogger
import org.koin.androidx.navigation.koinNavGraphViewModel
import java.util.*


class DocScanResultFragment : BaseFragment() {

    private val kycSharedViewModel: KycSharedViewModel by koinNavGraphViewModel(FourthlineFlow.navigationId)
    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineFlow.navigationId)

    private var docNumberTextInput: EditText? = null
    private var expireDateTextInput: DateInputTextView? = null
    private var expiryDateError: TextView? = null
    private var continueButton: Button? = null
    private var expireDateWatcher: TextWatcher? = null
    private var docNumberWatcher: TextWatcher? = null

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_doc_scan_result, container, false)
            .also {
                docNumberTextInput = it.findViewById(R.id.docNumberTextInput)
                expireDateTextInput = it.findViewById(R.id.expireDateTextInput)
                expiryDateError = it.findViewById(R.id.expireDateError)
                continueButton = it.findViewById(R.id.continueButton)
            }
    }

    override fun customizeView(view: View) {
        continueButton?.customize(customization)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        expireDateWatcher = expireDateTextInput?.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            updateContinueButtonState()
        })
        docNumberWatcher = docNumberTextInput?.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            updateContinueButtonState()
        })


        kycSharedViewModel.getKycDocument().let { doc ->
            IdLogger.info("Kyc Doc info was extracted: DocNumber: ${!doc.number.isNullOrEmpty()} " +
                    "ExpirationDate: ${doc.expirationDate != null}")
            docNumberTextInput!!.setText(doc.number)
            doc.expirationDate?.let { expireDateTextInput!!.setDate(it) }
        }

        continueButton!!.setOnClickListener {
            val expirationDate = expireDateTextInput!!.getDate()
            val number = docNumberTextInput!!.text.toString().filter { !it.isWhitespace() }

            if (expirationDate != null && number.isNotBlank()) {
                kycSharedViewModel.updateExpireDate(expirationDate)
                kycSharedViewModel.updateDocumentNumber(number)
                hideKeyboard()
                activityViewModel.onDocResultOutcome()
            } else {
                showGenericAlertFragment {  }
            }
        }
        continueButton!!.buttonDisabled(true)
    }

    override fun onDestroyView() {
        expireDateTextInput?.removeTextChangedListener(expireDateWatcher)
        docNumberTextInput?.removeTextChangedListener(docNumberWatcher)
        docNumberTextInput = null
        expireDateTextInput = null
        continueButton = null
        expiryDateError = null
        super.onDestroyView()
    }

    private fun updateContinueButtonState() {
        continueButton?.isEnabled = validateDateInputs()
        continueButton!!.buttonDisabled(!validateDateInputs())
    }

    private fun validateDateInputs(): Boolean {
        val expiryDate = expireDateTextInput?.getDate() ?: return false
        var valid = true
        if (expiryDate <= Date()) {
            expiryDateError!!.visibility = View.VISIBLE
            valid = false
        } else {
            expiryDateError!!.visibility = View.GONE
        }
        return !expireDateTextInput!!.text.isNullOrEmpty()
                && !docNumberTextInput!!.text.isNullOrEmpty()
                && valid
    }
}

