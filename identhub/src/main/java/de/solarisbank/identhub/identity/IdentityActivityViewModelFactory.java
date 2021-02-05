package de.solarisbank.identhub.identity;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.di.internal.Factory;

public class IdentityActivityViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;

    public IdentityActivityViewModelFactory(IdentityModule identityModule) {
        this.identityModule = identityModule;
    }

    public static IdentityActivityViewModelFactory create(IdentityModule identityModule) {
        return new IdentityActivityViewModelFactory(identityModule);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideIdentityActivityViewModel();
    }
}
