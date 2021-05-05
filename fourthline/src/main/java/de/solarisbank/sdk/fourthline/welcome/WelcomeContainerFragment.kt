package de.solarisbank.sdk.fourthline.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.solarisbank.sdk.core.viewModels
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent

class WelcomeContainerFragment : FourthlineFragment() {

    private var welcomeTitle: TextView? = null
    private var welcomePager: ViewPager? = null
    private var welcomeTabLayout: TabLayout? = null

    private val viewModel: WelcomeSharedViewModel by lazy<WelcomeSharedViewModel> { viewModels() }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_welcome_container, container, false)
        welcomeTitle = view.findViewById(R.id.welcomeTitle)
        welcomePager = view.findViewById(R.id.welcomePager)
        welcomeTabLayout = view.findViewById(R.id.welcomeTabLayout)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val savedPosition = viewModel.pagerPosition
        welcomePager!!.adapter = WelcomePagerAdapter(childFragmentManager)
        welcomePager!!.setCurrentItem(savedPosition, false)
        welcomePager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                viewModel.pagerPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        welcomeTabLayout!!.setupWithViewPager(welcomePager)
    }

    override fun onDestroyView() {
        welcomeTitle = null
        welcomePager = null
        welcomeTabLayout = null
        super.onDestroyView()
    }
}

