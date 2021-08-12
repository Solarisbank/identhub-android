package de.solarisbank.sdk.fourthline.feature.ui.terms

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel

class TermsAndConditionsFragment : FourthlineFragment() {

    private var termsText: TextView? = null
    private var submitButton: Button? = null

    private val activityViewModel: FourthlineViewModel by lazy<FourthlineViewModel> {
        activityViewModels()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_terms_and_condition, container, false)
                .also {
                    termsText = it.findViewById(R.id.termsText)
                    submitButton = it.findViewById(R.id.submitButton)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val terms = getString(R.string.fourthline_terms_conditions_apply)
        val termsLinkSection = getString(R.string.fourthline_terms_conditions_apply_link_section)
        var linkStartIndex = terms.indexOf(termsLinkSection)
        var linkEndIndex = linkStartIndex + termsLinkSection.length
        if (linkStartIndex == -1) {
            linkStartIndex = 0
            linkEndIndex = terms.length
        }
        val termsSpannable = SpannableString(terms)
        val link = getString(R.string.fourthline_terms_conditions_link)
        termsSpannable.setSpan(URLSpan(link), linkStartIndex, linkEndIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        termsText!!.text = termsSpannable
        termsText!!.movementMethod = LinkMovementMethod()
        submitButton!!.setOnClickListener { activityViewModel.navigateFromTcToWelcomeContainerFragment() }
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onDestroyView() {
        termsText = null
        submitButton = null
        super.onDestroyView()
    }
}
