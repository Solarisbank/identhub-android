package de.solarisbank.identhub.session.data.di;

import de.solarisbank.identhub.session.data.network.UserAgentInterceptor;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;

public class NetworkModuleProvideUserAgentInterceptorFactory implements Factory<UserAgentInterceptor> {

    private NetworkModuleProvideUserAgentInterceptorFactory() {

    }

    public static NetworkModuleProvideUserAgentInterceptorFactory create() {
        return new NetworkModuleProvideUserAgentInterceptorFactory();
    }

    @Override
    public UserAgentInterceptor get() {
        return Preconditions.checkNotNull(new UserAgentInterceptor());
    }
}
