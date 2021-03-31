package de.solarisbank.sdk.fourthline.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.solarisbank.sdk.core.view.viewBinding
import de.solarisbank.sdk.fourthline.FourthlineComponent
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.databinding.FragmentWelcomePageBinding
import de.solarisbank.sdk.fourthline.welcome.WelcomePageFragmentInjector.Companion.ARG_POSITION
import de.solarisbank.sdk.fourthline.welcome.WelcomePageFragmentInjector.Companion.PAGE_ONE_SELFIE
import de.solarisbank.sdk.fourthline.welcome.WelcomePageFragmentInjector.Companion.PAGE_THREE_LOCATION
import de.solarisbank.sdk.fourthline.welcome.WelcomePageFragmentInjector.Companion.PAGE_TWO_DOC_SCAN

class WelcomePageFragment : FourthlineFragment() {

    private val binding: FragmentWelcomePageBinding by viewBinding { FragmentWelcomePageBinding.inflate(layoutInflater) }

    override fun inject(component: FourthlineComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defineViewsValues(arguments?.getInt(ARG_POSITION))
    }

    private fun defineViewsValues(position: Int?) {
        when (position) {
            PAGE_ONE_SELFIE -> {
                binding.welcomeGuideImage.setImageResource(R.drawable.ic_welcome_page_selfie)
                binding.guideTitle.setText(R.string.welcome_page_1_guide_title)
                binding.guide.setText(R.string.welcome_page_1_guide)
            }
            PAGE_TWO_DOC_SCAN -> {
                binding.welcomeGuideImage.setImageResource(R.drawable.ic_welcome_page_doc_scan)
                binding.guideTitle.setText(R.string.welcome_page_2_guide_title)
                binding.guide.setText(R.string.welcome_page_2_guide)
            }
            PAGE_THREE_LOCATION -> {
                binding.welcomeGuideImage.setImageResource(R.drawable.ic_welcome_page_location)
                binding.guideTitle.setText(R.string.welcome_page_3_guide_title)
                binding.guide.setText(R.string.welcome_page_3_guide)
            }
        }
    }

}

