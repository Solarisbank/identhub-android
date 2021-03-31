package de.solarisbank.identhub.verfication.bank.gateway.processing;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.identity.IdentityModule;
import de.solarisbank.sdk.core.di.internal.Factory;

public final class ProcessingVerificationViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;

    public ProcessingVerificationViewModelFactory(IdentityModule identityModule) {
        this.identityModule = identityModule;
    }

    public static ProcessingVerificationViewModelFactory create(IdentityModule identityModule) {
        return new ProcessingVerificationViewModelFactory(identityModule);
    }

    @Override
    public ProcessingVerificationViewModel get() {
        return identityModule.provideProcessingVerificationViewModel();
    }
}
