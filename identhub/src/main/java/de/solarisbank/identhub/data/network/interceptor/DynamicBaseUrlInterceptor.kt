package de.solarisbank.identhub.data.network.interceptor

import de.solarisbank.identhub.domain.session.SessionUrlRepository
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class DynamicBaseUrlInterceptor(
        private val sessionUrlRepository: SessionUrlRepository
) : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()
        val httpBaseUrl = sessionUrlRepository.get()?.toHttpUrlOrNull()
        if (httpBaseUrl != null) {
            val updated =
                    original.url.toString().replace(DUMMY_BASE_URL, httpBaseUrl.toUrl().toString()).toHttpUrlOrNull()
                            ?: throw IllegalArgumentException("Problem with dummy base url replacement")
            original = original.newBuilder()
                    .url(updated)
                    .build()
        } else {
            throw IllegalArgumentException(
                    String.format(
                            "Base url is invalid: %s",
                            httpBaseUrl
                    )
            )
        }
        return chain.proceed(original)
    }

    companion object {
        const val DUMMY_BASE_URL = "http://localhost:8888/"
    }
}