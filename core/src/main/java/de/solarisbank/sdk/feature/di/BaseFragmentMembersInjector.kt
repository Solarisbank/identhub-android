package de.solarisbank.sdk.feature.di

import de.solarisbank.sdk.feature.base.BaseFragment
import de.solarisbank.sdk.feature.di.internal.MembersInjector

abstract class BaseFragmentMembersInjector<T : BaseFragment>(
    private val dependencies: BaseFragmentDependencies
    ) : MembersInjector<T> {
    override fun injectMembers(instance: T) {
        instance.assistedViewModelFactory = dependencies.assistedViewModelFactoryProvider.get()
        instance.customizationRepository = dependencies.customizationRepositoryProvider.get()
    }
}