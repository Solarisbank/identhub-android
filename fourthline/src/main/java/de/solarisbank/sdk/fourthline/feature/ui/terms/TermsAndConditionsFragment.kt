package de.solarisbank.sdk.fourthline.feature.ui.terms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import de.solarisbank.identhub.session.main.NewBaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.extension.linkOccurrenceOf
import de.solarisbank.sdk.feature.view.BulletListLayout
import de.solarisbank.sdk.fourthline.FourthlineModule
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import org.koin.androidx.navigation.koinNavGraphViewModel

class TermsAndConditionsFragment : NewBaseFragment() {

    private var bulletList: BulletListLayout? = null
    private var submitButton: Button? = null
    private var condition1ImageView: ImageView? = null
    private var condition2ImageView: ImageView? = null

    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_terms_and_condition, container, false)
                .also {
                    bulletList = it.findViewById(R.id.bulletList)
                    submitButton = it.findViewById(R.id.submitButton)
                    condition1ImageView = it.findViewById(R.id.condition1ImageView)
                    condition2ImageView = it.findViewById(R.id.condition2ImageView)
                    customizeUI()
                }
    }

    private fun customizeUI() {
        submitButton?.customize(customization)
        condition1ImageView?.customize(customization)
        condition2ImageView?.customize(customization)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val terms = getString(R.string.identhub_fourthline_terms_conditions_apply)
        val termsLinkSection = getString(R.string.identhub_fourthline_terms_conditions_apply_link_section)
        val link = getString(R.string.identhub_fourthline_terms_conditions_link)
        val termsSpanned = terms.linkOccurrenceOf(termsLinkSection, link, linkAllIfNotFound = true)
        bulletList?.updateItems(
            title = getString(R.string.identhub_fourthline_terms_notice_title),
            items = listOf(termsSpanned),
            titleStyle = BulletListLayout.TitleStyle.Notice,
            customization = customization
        )
        submitButton?.setOnClickListener { activityViewModel.onTermsOutcome() }
    }

    override fun onDestroyView() {
        bulletList = null
        submitButton = null
        condition1ImageView = null
        condition2ImageView = null
        super.onDestroyView()
    }
}
