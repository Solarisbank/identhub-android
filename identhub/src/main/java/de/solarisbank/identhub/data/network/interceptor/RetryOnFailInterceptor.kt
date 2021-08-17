package de.solarisbank.identhub.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber

class RetryOnFailInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        var response = chain.proceed(request)
        var tryCount = 0
        while (!response.isSuccessful && tryCount < 3) {
            Timber.d("Request is not successful - $tryCount")
            tryCount++
            response = chain.proceed(request)
        }
        return response
    }
}