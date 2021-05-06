package de.solarisbank.identhub.di.network;

import de.solarisbank.identhub.data.network.interceptor.UserAgentInterceptor;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;

public class NetworkModuleProvideUserAgentInterceptorFactory implements Factory<UserAgentInterceptor> {

    private final NetworkModule networkModule;

    private NetworkModuleProvideUserAgentInterceptorFactory(NetworkModule networkModule) {
        this.networkModule = networkModule;
    }

    public static NetworkModuleProvideUserAgentInterceptorFactory create(NetworkModule networkModule) {
        return new NetworkModuleProvideUserAgentInterceptorFactory(networkModule);
    }

    @Override
    public UserAgentInterceptor get() {
        return Preconditions.checkNotNull(networkModule.provideUserAgentInterceptor());
    }
}
