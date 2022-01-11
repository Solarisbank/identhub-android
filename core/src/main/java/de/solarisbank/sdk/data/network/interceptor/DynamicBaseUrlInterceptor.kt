package de.solarisbank.sdk.data.network.interceptor

import de.solarisbank.sdk.data.repository.SessionUrlRepository
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

class DynamicBaseUrlInterceptor(
        private val sessionUrlRepository: SessionUrlRepository
) : Interceptor {


    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var originalRequest = chain.request()
        Timber.d("intercept, originalRequest : $originalRequest")
        sessionUrlRepository.get()?.toHttpUrl().toString().let {
            val updated =
                originalRequest.url.toString().replace(DUMMY_BASE_URL, it)
            Timber.d("intercept, updated : $updated")
            originalRequest =
                originalRequest.newBuilder().url(updated).build()
        }
        return chain.proceed(originalRequest)
    }

    companion object {
        const val DUMMY_BASE_URL = "http://localhost:8888/"
    }
}