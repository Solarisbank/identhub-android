package de.solarisbank.identhub.contract.sign;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.identity.IdentityModule;

public class ContractSigningViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;

    public ContractSigningViewModelFactory(IdentityModule identityModule) {
        this.identityModule = identityModule;
    }

    public static ContractSigningViewModelFactory create(IdentityModule identityModule) {
        return new ContractSigningViewModelFactory(identityModule);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideContractSigningViewModel();
    }
}
