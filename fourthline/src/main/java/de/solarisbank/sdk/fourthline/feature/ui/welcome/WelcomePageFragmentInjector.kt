package de.solarisbank.sdk.fourthline.feature.ui.welcome

import de.solarisbank.sdk.feature.di.BaseFragmentDependencies
import de.solarisbank.sdk.feature.di.BaseFragmentMembersInjector
import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class WelcomePageFragmentInjector(
        baseDependencies: BaseFragmentDependencies
) : BaseFragmentMembersInjector<WelcomePageFragment>(baseDependencies) {

    companion object {
        internal const val ARG_POSITION = "position"
        internal const val PAGE_ONE_SELFIE = 1
        internal const val PAGE_TWO_DOC_SCAN = 2
        internal const val PAGE_THREE_LOCATION = 3
    }

}