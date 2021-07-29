package de.solarisbank.sdk.fourthline.feature.ui.passing.possibility

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

class PassingPossibilityFragmentInjector private constructor(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
) : MembersInjector<PassingPossibilityFragment> {

    override fun injectMembers(instance: PassingPossibilityFragment) {
        PassingPossibilityFragmentInjector.injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {

        fun injectAssistedViewModelFactory(instance: PassingPossibilityFragment, saveViewModelFactory: AssistedViewModelFactory?) {
            instance.assistedViewModelFactory = saveViewModelFactory!!
        }
    }
}