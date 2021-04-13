package de.solarisbank.sdk.fourthline.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import de.solarisbank.sdk.fourthline.welcome.WelcomePageFragmentInjector.Companion.ARG_POSITION

class WelcomePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    companion object {
        private const val PAGES_AMOUNT = 3
    }

    override fun getCount(): Int {
        return PAGES_AMOUNT
    }

    override fun getItem(position: Int): Fragment {
        return WelcomePageFragment().apply {
            arguments = Bundle().apply { putInt(ARG_POSITION, position + 1) }
        }
    }
}