package de.solarisbank.sdk.fourthline.feature.ui.welcome

import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class WelcomePageFragmentInjector(
        private val assistedViewModelFactory: Provider<AssistedViewModelFactory>
) : MembersInjector<WelcomePageFragment> {

    override fun injectMembers(instance: WelcomePageFragment) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactory.get())
    }

    companion object {
        internal const val ARG_POSITION = "position"
        internal const val PAGE_ONE_SELFIE = 1
        internal const val PAGE_TWO_DOC_SCAN = 2
        internal const val PAGE_THREE_LOCATION = 3


        @JvmStatic
        fun injectAssistedViewModelFactory(
                instance: WelcomePageFragment,
                assistedViewModelFactory: AssistedViewModelFactory
        ) {
            instance.assistedViewModelFactory = assistedViewModelFactory
        }
    }
}