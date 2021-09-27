package de.solarisbank.sdk.data.di.network;

import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import retrofit2.converter.moshi.MoshiConverterFactory;

public final class NetworkModuleProvideMoshiConverterFactory implements Factory<MoshiConverterFactory> {

    private final NetworkModule networkModule;

    public NetworkModuleProvideMoshiConverterFactory(
            NetworkModule networkModule) {
        this.networkModule = networkModule;
    }

    public static NetworkModuleProvideMoshiConverterFactory create(
            NetworkModule networkModule
    ) {
        return new NetworkModuleProvideMoshiConverterFactory(networkModule);
    }

    @Override
    public MoshiConverterFactory get() {
        return Preconditions.checkNotNull(
                networkModule.provideMoshiConverterFactory(),
                "Cannot return null from provider method"
        );
    }
}
