package de.solarisbank.identhub.di.network;

import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import retrofit2.CallAdapter;

public final class NetworkModuleProvideRxJavaCallAdapterFactory implements Factory<CallAdapter.Factory> {

    private final NetworkModule networkModule;

    public NetworkModuleProvideRxJavaCallAdapterFactory(
            NetworkModule networkModule) {
        this.networkModule = networkModule;
    }

    public static NetworkModuleProvideRxJavaCallAdapterFactory create(
            NetworkModule networkModule
    ) {
        return new NetworkModuleProvideRxJavaCallAdapterFactory(networkModule);
    }

    @Override
    public CallAdapter.Factory get() {
        return Preconditions.checkNotNull(
                networkModule.provideRxJavaCallAdapterFactory(),
                "Cannot return null from provider method"
        );
    }
}
