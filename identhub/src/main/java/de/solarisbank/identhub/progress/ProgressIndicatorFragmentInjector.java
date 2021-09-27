package de.solarisbank.identhub.progress;

import de.solarisbank.sdk.feature.di.internal.MembersInjector;
import de.solarisbank.sdk.feature.di.internal.Provider;
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory;

public final class ProgressIndicatorFragmentInjector implements MembersInjector<ProgressIndicatorFragment> {

    private final Provider<AssistedViewModelFactory> assistedViewModelFactory;

    public ProgressIndicatorFragmentInjector(Provider<AssistedViewModelFactory> assistedViewModelFactory) {
        this.assistedViewModelFactory = assistedViewModelFactory;
    }

    public static void injectAssistedViewModelFactory(ProgressIndicatorFragment instance, AssistedViewModelFactory assistedViewModelFactory) {
        instance.assistedViewModelFactory = assistedViewModelFactory;
    }

    @Override
    public void injectMembers(ProgressIndicatorFragment instance) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactory.get());
    }
}
