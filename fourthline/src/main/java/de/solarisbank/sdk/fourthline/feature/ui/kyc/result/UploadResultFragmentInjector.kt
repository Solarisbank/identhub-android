package de.solarisbank.sdk.fourthline.feature.ui.kyc.result

import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class UploadResultFragmentInjector private constructor(
    private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
)  :
    MembersInjector<UploadResultFragment> {

    override fun injectMembers(instance: UploadResultFragment) {
        injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {

        fun injectAssistedViewModelFactory(instance: UploadResultFragment, saveStateViewModelFactory: AssistedViewModelFactory?) {
            instance.assistedViewModelFactory = saveStateViewModelFactory!!
        }
    }
}