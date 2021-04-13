package de.solarisbank.sdk.fourthline.di

import de.solarisbank.sdk.fourthline.selfie.SelfieFragment
import de.solarisbank.sdk.fourthline.selfie.SelfieResultFragment
import de.solarisbank.sdk.fourthline.terms.TermsAndConditionsFragment
import de.solarisbank.sdk.fourthline.welcome.WelcomeContainerFragment
import de.solarisbank.sdk.fourthline.welcome.WelcomePageFragment

interface FourthlineFragmentComponent {

    fun inject(termsAndConditionsFragment: TermsAndConditionsFragment)

    fun inject(welcomeContainerFragment: WelcomeContainerFragment)

    fun inject(welcomePageFragment: WelcomePageFragment)

    fun inject(selfieFragment: SelfieFragment)

    fun inject(selfieResultFragment: SelfieResultFragment)

    interface Factory {
        fun create(): FourthlineFragmentComponent
    }
}