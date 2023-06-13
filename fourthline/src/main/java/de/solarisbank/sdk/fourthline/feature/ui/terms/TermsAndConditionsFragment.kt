package de.solarisbank.sdk.fourthline.feature.ui.terms

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.extension.buttonDisabled
import de.solarisbank.sdk.fourthline.FourthlineModule
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.terms.adapter.Slide
import de.solarisbank.sdk.fourthline.feature.ui.terms.adapter.SlideAdapter
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TermsAndConditionsFragment : BaseFragment() {

    private var introSlider: ViewPager2? = null
    private var slideIndicator: LinearLayout? = null
    private var slideAdapter: SlideAdapter? = null

    private var submitButton: Button? = null
    private var checkBox: AppCompatCheckBox? = null
    private var termsAndConditionsTextView: TextView? = null
    private var privacyStatementTextView: TextView? = null
    private var namirialLayout: View? = null

    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineModule.navigationId)
    private val viewModel: TermsAndConditionsViewModel by viewModel()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_terms_and_condition, container, false)
                .also {
                    submitButton = it.findViewById(R.id.submitButton)
                    checkBox = it.findViewById(R.id.namirialTermsCheckBox)
                    checkBox?.setOnCheckedChangeListener { _, _ -> updateSubmitButtonState()}
                    termsAndConditionsTextView = it.findViewById(R.id.namirialTermsDescription)
                    termsAndConditionsTextView?.movementMethod = LinkMovementMethod.getInstance()
                    privacyStatementTextView = it.findViewById(R.id.fourthlinePrivacyDescription)
                    privacyStatementTextView?.movementMethod = LinkMovementMethod.getInstance()
                    namirialLayout = it.findViewById(R.id.namirialTermsLayout)
                    introSlider = it.findViewById(R.id.introViewPager)
                    slideIndicator = it.findViewById(R.id.slideIndicator)
                    initView()
                }
    }

    override fun customizeView(view: View) {
        submitButton?.customize(customization)
        checkBox?.customize(customization)
    }

    private fun updateSubmitButtonState() {
        submitButton!!.isEnabled = checkBox!!.isChecked
        submitButton?.buttonDisabled(!submitButton!!.isEnabled)
    }

    private fun initView() {
        updateSubmitButtonState()
        submitButton?.setOnClickListener { activityViewModel.onTermsOutcome() }
        viewModel.state().observe(viewLifecycleOwner) {
            namirialLayout?.isVisible = it.shouldShowNamirialTerms
            if (!it.shouldShowNamirialTerms) {
                submitButton?.buttonDisabled(false)
            }
        }

        slideAdapter = SlideAdapter(requireContext(), getSlides(), customization)
        introSlider?.adapter = slideAdapter

        addSlideIndicators()
        setSlideIndicator(0)

        introSlider!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setSlideIndicator(position)
            }
        })
    }

    private fun getSlides(): List<Slide> {
        return listOf(
            Slide(R.drawable.identhub_ic_welcome_page_doc_scan,
                R.string.identhub_fourthline_intro_slide_title_one,
                R.string.identhub_fourthline_intro_slide_description_one),
            Slide(R.drawable.identhub_ic_welcome_page_selfie,
                R.string.identhub_fourthline_intro_slide_title_two,
                R.string.identhub_fourthline_intro_slide_description_two),
            Slide(R.drawable.identhub_ic_welcome_page_location,
                R.string.identhub_fourthline_intro_slide_title_three,
                R.string.identhub_fourthline_intro_slide_description_three)
        )
    }

    private fun addSlideIndicators() {
        val slides = getSlides()
        val dotSize = resources.getDimensionPixelSize(R.dimen.identhub_slide_indicator_dot_size)
        val dotMargin = resources.getDimensionPixelSize(R.dimen.identhub_slide_indicator_dot_margin)

        for (i in slides.indices) {
            val dot = View(requireContext())
            dot.setBackgroundResource(R.drawable.identhub_default_pager_dot)
            val params = LinearLayout.LayoutParams(dotSize, dotSize)
            params.setMargins(dotMargin, 0, dotMargin, 0)
            dot.layoutParams = params
            slideIndicator!!.addView(dot)
        }
    }

    private fun setSlideIndicator(position: Int) {
        val slidesCount = slideIndicator!!.childCount

        for (i in 0 until slidesCount) {
            val dot = slideIndicator!!.getChildAt(i)
            dot.setBackgroundResource(if (i == position) R.drawable.identhub_selected_pager_dot else R.drawable.identhub_default_pager_dot)
        }
    }

    override fun onDestroyView() {
        submitButton = null
        checkBox = null
        termsAndConditionsTextView = null
        privacyStatementTextView = null
        slideAdapter = null
        introSlider = null
        slideIndicator = null
        super.onDestroyView()
    }
}
