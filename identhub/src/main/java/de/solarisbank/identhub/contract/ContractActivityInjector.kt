package de.solarisbank.identhub.contract

import de.solarisbank.sdk.feature.di.internal.MembersInjector
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class ContractActivityInjector(private val assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory>) :
    MembersInjector<ContractActivity> {

    override fun injectMembers(instance: ContractActivity) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactoryProvider.get())
    }

    companion object {
        @JvmStatic
        fun injectAssistedViewModelFactory(contractActivity: ContractActivity, assistedViewModelFactory: AssistedViewModelFactory) {
            contractActivity.assistedViewModelFactory = assistedViewModelFactory
        }
    }
}