package de.solarisbank.identhub.startup.feature.termsandcondition

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.identhub.session.module.ModuleOutcome
import de.solarisbank.identhub.session.module.outcome.TermsModuleOutcome
import de.solarisbank.identhub.startup.R
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.customization.customizeLinks
import de.solarisbank.sdk.feature.extension.buttonDisabled
import de.solarisbank.sdk.feature.extension.linkOccurrenceOf

class TermsAndConditionsFragment : BaseFragment() {

    private var continueButton: Button? = null
    private var termsCheckBox: AppCompatCheckBox? = null
    private var termsDisclaimer: TextView? = null

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.identhub_fragment_terms_and_conditions, container, false)
            .also {
                continueButton = it.findViewById(R.id.continueButton)
                continueButton?.setOnClickListener {
                    navigator?.onOutcome(ModuleOutcome.Other(TermsModuleOutcome()))
                }
                termsCheckBox = it.findViewById(R.id.termsCheckBox)
                termsCheckBox?.setOnCheckedChangeListener { _, _ -> updateContinueButtonState() }
                termsDisclaimer = it.findViewById(R.id.termsDescription)
                updateContinueButtonState()
                setTermsText()
            }
    }

    override fun customizeView(view: View) {
        continueButton?.customize(customization)
        termsCheckBox?.customize(customization)
        termsDisclaimer?.customizeLinks(customization)
    }

    private fun updateContinueButtonState() {
        continueButton!!.isEnabled = termsCheckBox!!.isChecked
        continueButton?.buttonDisabled(!continueButton!!.isEnabled)
    }

    private fun setTermsText() {
        val termsPartText = getString(R.string.identhub_terms_agreement_terms)
        val privacyPartText = getString(R.string.identhub_terms_agreement_privacy)
        val termsText = getString(R.string.identhub_terms_agreement, termsPartText, privacyPartText)
        val spanned = termsText.linkOccurrenceOf(termsPartText, termsLink)
        spanned.linkOccurrenceOf(privacyPartText, privacyLink)
        termsDisclaimer?.text = spanned
        termsDisclaimer?.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDestroyView() {
        continueButton = null
        termsCheckBox = null
        termsDisclaimer = null
        super.onDestroyView()
    }

    companion object {
        private const val privacyLink = "https://www.solarisbank.com/en/privacy-policy/"
        private const val termsLink = "https://www.solarisbank.com/en/customer-information/"
    }
}