package de.solarisbank.sdk.data.network.interceptor

import de.solarisbank.sdk.data.repository.SessionUrlRepository
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class DynamicBaseUrlInterceptor(
    private val sessionUrlRepository: SessionUrlRepository
) : Interceptor {


    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val sessionURL = sessionUrlRepository.get()?.toHttpUrl().toString()

        val updatedURL = chain.request().url.toString().replace(DUMMY_BASE_URL, sessionURL)

        var headerUpdated = chain.request().newBuilder()
            .url(updatedURL)
            .build()

        val sessionID = headerUpdated.url.pathSegments[1]
        val sessionRemovedUrl = headerUpdated.url.toString().replace("$sessionID/", "")

        headerUpdated = headerUpdated.newBuilder()
            .addHeader(SOLARIS_SESSION_TOKEN_HEADER_NAME, sessionID)
            .url(sessionRemovedUrl)
            .build()

        return chain.proceed(headerUpdated)
    }

    companion object {
        const val DUMMY_BASE_URL = "http://localhost:8888/"
        private const val SOLARIS_SESSION_TOKEN_HEADER_NAME = "x-solaris-session-token"
    }
}