package de.solarisbank.sdk.logger

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class LoggerHttpInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response = chain.proceed(request)
        if (response.code in 400..500 && !request.url.toString()
                .contains("sdk_logging")
        ) { //Filtering out logging failures.

            IdLogger.logError("Network Error:" + "${request.url}" + "\n ${response.body}")
        }
        return response
    }
}