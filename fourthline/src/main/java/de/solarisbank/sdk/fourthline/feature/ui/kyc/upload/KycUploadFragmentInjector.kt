package de.solarisbank.sdk.fourthline.feature.ui.kyc.upload

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

class KycUploadFragmentInjector(
        private val viewModelFactoryProvider: Provider<AssistedViewModelFactory>
) : MembersInjector<KycUploadFragment> {
    override fun injectMembers(instance: KycUploadFragment) {
        KycUploadFragmentInjector.injectAssistedViewModelFactory(instance, viewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(instance: KycUploadFragment, saveStateViewModelFactory: AssistedViewModelFactory?) {
            instance.assistedViewModelFactory = saveStateViewModelFactory!!
        }
    }

}