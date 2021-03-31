package de.solarisbank.identhub.identity;

import de.solarisbank.sdk.core.di.internal.MembersInjector;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory;

public final class IdentityActivityInjector implements MembersInjector<IdentityActivity> {

    private final Provider<AssistedViewModelFactory> assistedViewModelFactory;

    public IdentityActivityInjector(Provider<AssistedViewModelFactory> assistedViewModelFactory) {
        this.assistedViewModelFactory = assistedViewModelFactory;
    }

    public static void injectAssistedViewModelFactory(IdentityActivity instance, AssistedViewModelFactory assistedViewModelFactory) {
        instance.assistedViewModelFactory = assistedViewModelFactory;
    }

    @Override
    public void injectMembers(IdentityActivity instance) {
        injectAssistedViewModelFactory(instance, assistedViewModelFactory.get());
    }
}
