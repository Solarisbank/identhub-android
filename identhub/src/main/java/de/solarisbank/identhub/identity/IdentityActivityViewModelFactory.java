package de.solarisbank.identhub.identity;

import androidx.lifecycle.ViewModel;

import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Provider;

public final class IdentityActivityViewModelFactory implements Factory<ViewModel> {
    private final IdentityModule identityModule;
    private final Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider;
    private final Provider<IdentificationStepPreferences> identificationStepPreferencesProvider;

    public IdentityActivityViewModelFactory(IdentityModule identityModule,
                                            Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider,
                                            Provider<IdentificationStepPreferences> identificationStepPreferencesProvider) {
        this.identityModule = identityModule;
        this.getIdentificationUseCaseProvider = getIdentificationUseCaseProvider;
        this.identificationStepPreferencesProvider = identificationStepPreferencesProvider;
    }

    public static IdentityActivityViewModelFactory create(IdentityModule identityModule, Provider<GetIdentificationUseCase> getIdentificationUseCaseProvider, Provider<IdentificationStepPreferences> identificationStepPreferencesProvider) {
        return new IdentityActivityViewModelFactory(identityModule, getIdentificationUseCaseProvider, identificationStepPreferencesProvider);
    }

    @Override
    public ViewModel get() {
        return identityModule.provideIdentityActivityViewModel(getIdentificationUseCaseProvider.get(), identificationStepPreferencesProvider.get());
    }
}
