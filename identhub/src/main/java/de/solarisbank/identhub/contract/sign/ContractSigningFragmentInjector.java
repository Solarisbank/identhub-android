package de.solarisbank.identhub.contract.sign;

import de.solarisbank.identhub.AssistedViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

public class ContractSigningFragmentInjector implements MembersInjector<ContractSigningFragment> {

    private final Provider<AssistedViewModelFactory> assistedViewModelFactory;

    public ContractSigningFragmentInjector(Provider<AssistedViewModelFactory> assistedViewModelFactory) {
        this.assistedViewModelFactory = assistedViewModelFactory;
    }

    public static void injectAssistedViewModelFactory(ContractSigningFragment instance, AssistedViewModelFactory assistedViewModelFactory) {
        instance.assistedViewModelFactory = assistedViewModelFactory;
    }

    @Override
    public void injectMembers(ContractSigningFragment instance) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactory.get());
    }
}
