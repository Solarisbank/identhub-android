package de.solarisbank.identhub.fourthline.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import de.solarisbank.identhub.base.BaseFragment
import de.solarisbank.identhub.base.view.viewBinding
import de.solarisbank.identhub.base.viewModels
import de.solarisbank.identhub.databinding.FragmentWelcomeContainerBinding
import de.solarisbank.identhub.di.FragmentComponent

class WelcomeContainerFragment : BaseFragment() {

    private val binding: FragmentWelcomeContainerBinding by viewBinding(FragmentWelcomeContainerBinding::inflate)
    private val viewModel: WelcomeSharedViewModel by lazy { viewModels() }

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val savedPosition = viewModel.pagerPosition
        binding.welcomePager.also {
            it.adapter = WelcomePagerAdapter(this)
            it.setCurrentItem(savedPosition, false)
            it.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.pagerPosition = position
                }
            })
        }

        TabLayoutMediator(binding.welcomeTabLayout, binding.welcomePager) {
            _, _ ->
        }.attach()
    }
}

