package de.solarisbank.identhub.data.network.interceptor

import de.solarisbank.identhub.BuildConfig
import de.solarisbank.identhub.session.IdentHubSession
import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sdkVersion = BuildConfig.SDK_VERSION
        val appName = IdentHubSession.appName
        val requestWithUserAgent = chain.request()
                .newBuilder()
                .addHeader("User-Agent", "IdentHub Android ($sdkVersion) - $appName")
                .build()
        return chain.proceed(requestWithUserAgent)
    }

}