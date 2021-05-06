package de.solarisbank.identhub.di.network;

import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import okhttp3.logging.HttpLoggingInterceptor;

public class NetworkModuleProvideHttpLoggingInterceptorFactory implements Factory<HttpLoggingInterceptor> {

    private final NetworkModule networkModule;

    private NetworkModuleProvideHttpLoggingInterceptorFactory(NetworkModule networkModule) {
        this.networkModule = networkModule;
    }

    public static NetworkModuleProvideHttpLoggingInterceptorFactory create(NetworkModule networkModule) {
        return new NetworkModuleProvideHttpLoggingInterceptorFactory(networkModule);
    }

    @Override
    public HttpLoggingInterceptor get() {
        return Preconditions.checkNotNull(networkModule.provideHttpLoggingInterceptor());
    }
}
