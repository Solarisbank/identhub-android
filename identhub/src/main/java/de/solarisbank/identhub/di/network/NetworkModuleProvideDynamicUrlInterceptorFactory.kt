package de.solarisbank.identhub.di.network

import de.solarisbank.identhub.data.network.interceptor.DynamicBaseUrlInterceptor
import de.solarisbank.identhub.di.internal.Factory
import de.solarisbank.identhub.di.internal.Preconditions
import de.solarisbank.identhub.di.internal.Provider
import de.solarisbank.identhub.domain.session.SessionUrlRepository

class NetworkModuleProvideDynamicUrlInterceptorFactory(
        private val networkModule: NetworkModule,
        private val sessionUrlRepositoryProvider: Provider<SessionUrlRepository>
) : Factory<DynamicBaseUrlInterceptor> {
    override fun get(): DynamicBaseUrlInterceptor {
        return Preconditions.checkNotNull(
                networkModule.provideDynamicBaseUrlInterceptor(sessionUrlRepositoryProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
                networkModule: NetworkModule,
                sessionUrlRepositoryProvider: Provider<SessionUrlRepository>
        ): NetworkModuleProvideDynamicUrlInterceptorFactory {
            return NetworkModuleProvideDynamicUrlInterceptorFactory(networkModule, sessionUrlRepositoryProvider)
        }
    }
}