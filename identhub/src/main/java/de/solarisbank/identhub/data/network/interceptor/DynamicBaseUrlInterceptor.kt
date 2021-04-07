package de.solarisbank.identhub.data.network.interceptor

import de.solarisbank.identhub.domain.session.SessionUrlRepository
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class DynamicBaseUrlInterceptor(
        private val sessionUrlRepository: SessionUrlRepository
) : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()
        val httpBaseUrl = toHttpUrlOrNull(sessionUrlRepository.get())
        if (httpBaseUrl != null) {
            val updated = original.url()
                    .toString()
                    .replace(DUMMY_BASE_URL, httpBaseUrl.toString())
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

    private fun toHttpUrlOrNull(url: String?): HttpUrl? {
        if (url == null) {
            return null
        }
        return HttpUrl.parse(url)
    }

    companion object {
        const val DUMMY_BASE_URL = "http://localhost:8888/"
    }
}