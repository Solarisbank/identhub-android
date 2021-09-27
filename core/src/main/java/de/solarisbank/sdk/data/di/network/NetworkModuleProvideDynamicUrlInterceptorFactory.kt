package de.solarisbank.sdk.data.di.network

import de.solarisbank.sdk.data.network.interceptor.DynamicBaseUrlInterceptor
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

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