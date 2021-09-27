package de.solarisbank.sdk.data.di.network;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public final class NetworkModuleProvideOkHttpClientFactory implements Factory<OkHttpClient> {

    private final NetworkModule networkModule;
    private final List<? extends @NotNull Interceptor> interceptors;

    public NetworkModuleProvideOkHttpClientFactory(
            NetworkModule networkModule,
            Provider<? extends @NotNull Interceptor>... interceptorProviders
    ) {
        this.networkModule = networkModule;

        List<Interceptor> tempInterceptors = new ArrayList<>();
        for (Provider<? extends @NotNull Interceptor> provider: interceptorProviders) {
            tempInterceptors.add(provider.get());
        }

        this.interceptors = tempInterceptors;
    }

    public static NetworkModuleProvideOkHttpClientFactory create(
            NetworkModule networkModule,
            @NotNull Provider<@NotNull ? extends @NotNull Interceptor>... interceptorProviders
    ) {
        return new NetworkModuleProvideOkHttpClientFactory(networkModule, interceptorProviders);
    }

    @Override
    public OkHttpClient get() {
        return Preconditions.checkNotNull(
                networkModule.provideOkHttpClient(interceptors),
                "Cannot return null from provider method"
        );
    }
}
