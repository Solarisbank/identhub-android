package de.solarisbank.sdk.fourthline.data.network

import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import okhttp3.Interceptor
import okhttp3.Response

class IdentificationIdInterceptor(
        private val identificationLocalDataSource: IdentificationLocalDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()

        if (original.url().toString().contains(IDENTIFICATION_ID_STUB)) {
            var updated = ""
            identificationLocalDataSource.obtainIdentificationDto().blockingGet()
                    .let {
                updated = original.url()
                        .toString()
                        .replace(IDENTIFICATION_ID_STUB, it.id)
            }
            original = original.newBuilder()
                    .url(updated)
                    .build()
        }
        return chain.proceed(original)
    }

    companion object {
        private const val IDENTIFICATION_ID_STUB = "identification_id"
    }

}