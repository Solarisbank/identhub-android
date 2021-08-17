package de.solarisbank.identhub.di.network

import de.solarisbank.identhub.data.network.interceptor.RetryOnFailInterceptor
import de.solarisbank.sdk.core.di.internal.Factory

class RetryOnFailInterceptorFactory : Factory<RetryOnFailInterceptor> {

    override fun get(): RetryOnFailInterceptor {
        return RetryOnFailInterceptor()
    }

    companion object {
        @JvmStatic
        fun create(): RetryOnFailInterceptorFactory {
            return RetryOnFailInterceptorFactory()
        }
    }
}