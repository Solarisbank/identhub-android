package de.solarisbank.identhub.contract.sign;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class ContractSigningFragmentInjector implements MembersInjector<ContractSigningFragment> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public ContractSigningFragmentInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(ContractSigningFragment instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(ContractSigningFragment instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
