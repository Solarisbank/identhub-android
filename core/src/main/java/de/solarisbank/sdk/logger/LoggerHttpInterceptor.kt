package de.solarisbank.sdk.logger

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class LoggerHttpInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response = chain.proceed(request)
        if (response.code in 400..500 && !request.url.toString().contains("sdk_logging")
        ) { //Filtering out logging failures.

            val responseBodyString = try {
                response.body?.string()
            } catch(throwable: Throwable) {
                null
            } ?: ""

            IdLogger.error(
                message = "Network Error. [Url]: ${request.url}, \n [Response]: $responseBodyString",
                category = IdLogger.Category.Api
            )
        }

        return response
    }
}