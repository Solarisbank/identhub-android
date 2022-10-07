package de.solarisbank.sdk.fourthline.feature.ui.terms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.isVisible
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.extension.linkOccurrenceOf
import de.solarisbank.sdk.feature.view.BulletListLayout
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel

class TermsAndConditionsFragment : FourthlineFragment() {

    private var bulletList: BulletListLayout? = null
    private var submitButton: Button? = null
    private var imageView: ImageView? = null
    private var condition1ImageView: ImageView? = null
    private var condition2ImageView: ImageView? = null

    private val activityViewModel: FourthlineViewModel by lazy {
        activityViewModels()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_terms_and_condition, container, false)
                .also {
                    bulletList = it.findViewById(R.id.bulletList)
                    submitButton = it.findViewById(R.id.submitButton)
                    imageView = it.findViewById(R.id.scratch)
                    condition1ImageView = it.findViewById(R.id.condition1ImageView)
                    condition2ImageView = it.findViewById(R.id.condition2ImageView)
                    customizeUI()
                }
    }

    private fun customizeUI() {
        imageView?.isVisible = customization.customFlags.shouldShowLargeImages
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
        submitButton?.setOnClickListener { activityViewModel.navigateFromTcToDocTypeSelection() }
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onDestroyView() {
        bulletList = null
        submitButton = null
        imageView = null
        condition1ImageView = null
        condition2ImageView = null
        super.onDestroyView()
    }
}
