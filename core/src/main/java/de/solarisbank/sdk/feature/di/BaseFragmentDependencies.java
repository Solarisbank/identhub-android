package de.solarisbank.sdk.feature.di;

import de.solarisbank.sdk.feature.customization.CustomizationRepository;
import de.solarisbank.sdk.feature.di.internal.Provider;
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory;

public class BaseFragmentDependencies {
    final Provider<AssistedViewModelFactory> assistedViewModelFactoryProvider;
    final Provider<CustomizationRepository> customizationRepositoryProvider;

    public BaseFragmentDependencies(
            Provider<AssistedViewModelFactory> assistedViewModelFactoryProvider,
            Provider<CustomizationRepository> customizationRepositoryProvider
    ) {
        this.assistedViewModelFactoryProvider = assistedViewModelFactoryProvider;
        this.customizationRepositoryProvider = customizationRepositoryProvider;
    }
}
