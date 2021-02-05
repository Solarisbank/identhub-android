package de.solarisbank.identhub.contract.preview;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.identity.IdentityModule;

public class ContractSigningPreviewViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;

    public ContractSigningPreviewViewModelFactory(IdentityModule identityModule) {
        this.identityModule = identityModule;
    }

    public static ContractSigningPreviewViewModelFactory create(IdentityModule identityModule) {
        return new ContractSigningPreviewViewModelFactory(identityModule);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideContractSigningPreviewViewModel();
    }
}
