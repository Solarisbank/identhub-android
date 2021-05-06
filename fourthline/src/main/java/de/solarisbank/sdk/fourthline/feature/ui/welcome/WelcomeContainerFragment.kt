package de.solarisbank.sdk.fourthline.feature.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.solarisbank.sdk.core.activityViewModels
import de.solarisbank.sdk.core.viewModels
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel

class WelcomeContainerFragment : FourthlineFragment() {

    private var welcomeTitle: TextView? = null
    private var welcomePager: ViewPager? = null
    private var welcomeTabLayout: TabLayout? = null
    private var startbutton: Button? = null

    private val viewModel: WelcomeSharedViewModel by lazy<WelcomeSharedViewModel> { viewModels() }

    private val sharedViewModel: FourthlineViewModel by lazy<FourthlineViewModel> {
        activityViewModels()
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome_container, container, false)
                .also {
                    welcomeTitle = it.findViewById(R.id.welcomeTitle)
                    welcomePager = it.findViewById(R.id.welcomePager)
                    welcomeTabLayout = it.findViewById(R.id.welcomeTabLayout)
                    startbutton = it.findViewById(R.id.startButton)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val savedPosition = viewModel.pagerPosition
        welcomePager.also {
            it!!.adapter = WelcomePagerAdapter(childFragmentManager)
            it.setCurrentItem(savedPosition, false)
            it.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    viewModel.pagerPosition = position
                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
        }

        welcomeTabLayout!!.setupWithViewPager(welcomePager)
        startbutton!!.setOnClickListener { sharedViewModel.navigateToSelfieFragment() }
    }

    override fun onDestroyView() {
        welcomeTitle = null
        welcomePager = null
        welcomeTabLayout = null
        startbutton = null
        super.onDestroyView()
    }
}

