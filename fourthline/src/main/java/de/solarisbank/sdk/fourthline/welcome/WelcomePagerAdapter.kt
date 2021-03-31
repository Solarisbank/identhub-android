package de.solarisbank.sdk.fourthline.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.solarisbank.sdk.fourthline.welcome.WelcomePageFragmentInjector.Companion.ARG_POSITION

class WelcomePagerAdapter(
        fragment: WelcomeContainerFragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return PAGES_AMOUNT
    }

    override fun createFragment(position: Int): Fragment {
        return WelcomePageFragment().apply {
            arguments = Bundle().apply { putInt(ARG_POSITION, position + 1) }
        }
    }

    companion object {
        private const val PAGES_AMOUNT = 3
    }
}