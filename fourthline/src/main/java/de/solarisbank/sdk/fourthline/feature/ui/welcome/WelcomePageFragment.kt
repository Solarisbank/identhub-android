package de.solarisbank.sdk.fourthline.feature.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomePageFragmentInjector.Companion.ARG_POSITION
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomePageFragmentInjector.Companion.PAGE_ONE_SELFIE
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomePageFragmentInjector.Companion.PAGE_THREE_LOCATION
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomePageFragmentInjector.Companion.PAGE_TWO_DOC_SCAN

class WelcomePageFragment : FourthlineFragment() {

    private var welcomeGuideImage: ImageView? = null
    private var guideTitle: TextView? = null
    private var guide: TextView? = null


    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome_page, container, false)
                .also {
                    welcomeGuideImage = it.findViewById(R.id.welcomeGuideImage)
                    guideTitle = it.findViewById(R.id.guideTitle)
                    guide = it.findViewById(R.id.guide)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defineViewsValues(arguments?.getInt(ARG_POSITION))
    }

    private fun defineViewsValues(position: Int?) {
        when (position) {
            PAGE_ONE_SELFIE -> {
                welcomeGuideImage!!.setImageResource(R.drawable.ic_welcome_page_selfie)
                guideTitle!!.setText(R.string.welcome_page_1_guide_title)
                guide!!.setText(R.string.welcome_page_1_guide)
            }
            PAGE_TWO_DOC_SCAN -> {
                welcomeGuideImage!!.setImageResource(R.drawable.ic_welcome_page_doc_scan)
                guideTitle!!.setText(R.string.welcome_page_2_guide_title)
                guide!!.setText(R.string.welcome_page_2_guide)
            }
            PAGE_THREE_LOCATION -> {
                welcomeGuideImage!!.setImageResource(R.drawable.ic_welcome_page_location)
                guideTitle!!.setText(R.string.welcome_page_3_guide_title)
                guide!!.setText(R.string.welcome_page_3_guide)
            }
        }
    }

    override fun onDestroyView() {
        welcomeGuideImage = null
        guideTitle = null
        guide = null
        super.onDestroyView()
    }

}

