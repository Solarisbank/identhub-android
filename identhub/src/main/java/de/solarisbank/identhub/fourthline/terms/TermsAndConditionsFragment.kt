package de.solarisbank.identhub.fourthline.terms

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.BaseFragment
import de.solarisbank.identhub.base.view.viewBinding
import de.solarisbank.identhub.base.viewModels
import de.solarisbank.identhub.databinding.FragmentTermsAndConditionBinding
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.shared.result.Event

class TermsAndConditionsFragment : BaseFragment() {
    private val binding: FragmentTermsAndConditionBinding by viewBinding(FragmentTermsAndConditionBinding::inflate)
    private val viewModel: TermsAndConditionsViewModel by lazy { viewModels() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeCheckboxChanged()
    }

    private fun observeCheckboxChanged() {
        viewModel.getTermsAndConditionCheckedEventLiveData().observe(viewLifecycleOwner, ::onTermsAndConditionCheckChanged)
    }

    private fun onTermsAndConditionCheckChanged(event: Event<Boolean>) {
        binding.submitButton.isEnabled = event.content ?: false
    }

    private fun initView() {
        binding.termsAndConditionCheckbox.makeLinks(
                Pair(getString(R.string.terms_and_condition_label), ::onTermsAndConditionsClicked),
                Pair(getString(R.string.privacy_statement_label), ::onPrivacyStatementClicked)
        )
        binding.termsAndConditionCheckbox.setOnCheckedChangeListener { _, isChecked -> viewModel.onTermsAndConditionCheckChanged(isChecked) }
    }

    private fun onTermsAndConditionsClicked() {
        TODO("Need to be implemented later")
    }

    private fun onPrivacyStatementClicked() {
        TODO("Need to be implemented later")
    }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onDestroyView() {
        binding.termsAndConditionCheckbox.removeLink()
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