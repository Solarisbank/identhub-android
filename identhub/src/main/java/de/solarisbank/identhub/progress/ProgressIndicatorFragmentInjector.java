package de.solarisbank.identhub.progress;

import de.solarisbank.identhub.AssistedViewModelFactory;
import de.solarisbank.identhub.di.internal.MembersInjector;
import de.solarisbank.identhub.di.internal.Provider;

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
