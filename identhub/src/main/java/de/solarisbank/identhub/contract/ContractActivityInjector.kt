package de.solarisbank.identhub.contract

import de.solarisbank.sdk.core.di.internal.MembersInjector
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

class ContractActivityInjector(private val assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory>) : MembersInjector<ContractActivity> {

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