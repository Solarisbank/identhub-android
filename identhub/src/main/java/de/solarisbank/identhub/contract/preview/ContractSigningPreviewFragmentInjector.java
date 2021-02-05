package de.solarisbank.identhub.contract.preview;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class ContractSigningPreviewFragmentInjector implements MembersInjector<ContractSigningPreviewFragment> {

    private final Provider<ViewModelFactory> viewModelFactoryProvider;

    public ContractSigningPreviewFragmentInjector(Provider<ViewModelFactory> viewModelFactoryProvider) {
        this.viewModelFactoryProvider = viewModelFactoryProvider;
    }

    public static void injectViewModelFactory(ContractSigningPreviewFragment instance, ViewModelFactory viewModelFactory) {
        instance.viewModelFactory = viewModelFactory;
    }

    @Override
    public void injectMembers(ContractSigningPreviewFragment instance) {
        injectViewModelFactory(instance, viewModelFactoryProvider.get());
    }
}
