package de.solarisbank.identhub.di.network;

import de.solarisbank.identhub.data.network.interceptor.DynamicBaseUrlInterceptor;
import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.di.internal.Preconditions;
import de.solarisbank.identhub.di.internal.Provider;
import okhttp3.OkHttpClient;

public final class NetworkModuleProvideOkHttpClientFactory implements Factory<OkHttpClient> {

    private final NetworkModule networkModule;
    private final Provider<DynamicBaseUrlInterceptor> dynamicBaseUrlInterceptor;

    public NetworkModuleProvideOkHttpClientFactory(
            NetworkModule networkModule,
            Provider<DynamicBaseUrlInterceptor> dynamicBaseUrlInterceptor
    ) {
        this.networkModule = networkModule;
        this.dynamicBaseUrlInterceptor = dynamicBaseUrlInterceptor;
    }

    public static NetworkModuleProvideOkHttpClientFactory create(
            NetworkModule networkModule,
            Provider<DynamicBaseUrlInterceptor> dynamicBaseUrlInterceptor
    ) {
        return new NetworkModuleProvideOkHttpClientFactory(networkModule, dynamicBaseUrlInterceptor);
    }

    @Override
    public OkHttpClient get() {
        return Preconditions.checkNotNull(
                networkModule.provideOkHttpClient(dynamicBaseUrlInterceptor.get()),
                "Cannot return null from provider method"
        );
    }
}
