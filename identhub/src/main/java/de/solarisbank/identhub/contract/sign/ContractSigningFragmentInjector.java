package de.solarisbank.identhub.contract.sign;

import de.solarisbank.sdk.feature.di.internal.MembersInjector;
import de.solarisbank.sdk.feature.di.internal.Provider;
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory;

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
