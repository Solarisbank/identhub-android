package de.solarisbank.sdk.fourthline.terms

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.core.viewModels
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent

class TermsAndConditionsFragment : FourthlineFragment() {

    private var submitButton: Button? = null
    private var termsAndConditionCheckbox: CheckBox? = null

    private val viewModel: TermsAndConditionsViewModel by lazy<TermsAndConditionsViewModel> { viewModels() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_terms_and_condition, container, false)
                .also {
                    submitButton = it.findViewById(R.id.submitButton)
                    termsAndConditionCheckbox = it.findViewById(R.id.termsAndConditionCheckbox)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeCheckboxChanged()
    }

    private fun observeCheckboxChanged() {
        viewModel.getTermsAndConditionCheckedEventLiveData().observe(viewLifecycleOwner, Observer { onTermsAndConditionCheckChanged(it) })
    }

    private fun onTermsAndConditionCheckChanged(event: Event<Boolean>) {
        submitButton!!.isEnabled = event.content ?: false
    }

    private fun initView() {
        termsAndConditionCheckbox!!.makeLinks(
                Pair(getString(R.string.terms_and_condition_label), ::onTermsAndConditionsClicked),
                Pair(getString(R.string.privacy_statement_label), ::onPrivacyStatementClicked)
        )
        termsAndConditionCheckbox!!.setOnCheckedChangeListener { _, isChecked -> viewModel.onTermsAndConditionCheckChanged(isChecked) }
    }

    private fun onTermsAndConditionsClicked() {
        TODO("Need to be implemented later")
    }

    private fun onPrivacyStatementClicked() {
        TODO("Need to be implemented later")
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onDestroyView() {
        termsAndConditionCheckbox!!.removeLink()
        termsAndConditionCheckbox = null
        submitButton = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(): Fragment {
            return TermsAndConditionsFragment()
        }
    }
}

fun CheckBox.makeLinks(vararg links: Pair<String, () -> Unit>) {
    val spannableString = SpannableString(this.text)
    for (link in links) {
        if (link.first.isNotEmpty()) {
            val clickableSpan = CheckboxClickableSpan(link.second)
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(
                    clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
    this.movementMethod = LinkMovementMethod.getInstance()
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun CheckBox.removeLink() {
    val spannable = text as SpannableString
    val spannableArray = spannable.getSpans(0, spannable.length, CheckboxClickableSpan::class.java)
    spannableArray.forEach {
        spannable.removeSpan(it)
        it.clear()
    }
    this.movementMethod = null
    this.text = null
}