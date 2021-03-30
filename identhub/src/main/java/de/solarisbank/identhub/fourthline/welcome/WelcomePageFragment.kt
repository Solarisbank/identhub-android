package de.solarisbank.identhub.fourthline.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.BaseFragment
import de.solarisbank.identhub.base.view.viewBinding
import de.solarisbank.identhub.databinding.FragmentWelcomePageBinding
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.fourthline.welcome.WelcomePageFragmentInjector.Companion.ARG_POSITION
import de.solarisbank.identhub.fourthline.welcome.WelcomePageFragmentInjector.Companion.PAGE_ONE_SELFIE
import de.solarisbank.identhub.fourthline.welcome.WelcomePageFragmentInjector.Companion.PAGE_THREE_LOCATION
import de.solarisbank.identhub.fourthline.welcome.WelcomePageFragmentInjector.Companion.PAGE_TWO_DOC_SCAN

class WelcomePageFragment : BaseFragment() {

    private val binding: FragmentWelcomePageBinding by viewBinding(FragmentWelcomePageBinding::inflate)

    override fun inject(component: FragmentComponent) {
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

