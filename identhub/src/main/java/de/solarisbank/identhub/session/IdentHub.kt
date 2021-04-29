package de.solarisbank.identhub.session

import android.net.Uri
import java.net.URI


object IdentHub {
    private var instance: IdentHubSession? = null

    val isPaymentResultAvailable: Boolean
        get() = instance?.isPaymentProcessAvailable ?: false

    fun sessionWithUrl(url: String): IdentHubSession {
        val uri = URI.create(url)
        val apiURL = buildUrl(uri)

        return IdentHubSession(apiURL).apply { instance = this }
    }

    private fun buildUrl(uri: URI): String {
        val domain = uri.authority.replaceFirst(".", "-api.")
        val apiUri = Uri.Builder().authority(domain)
                .scheme(uri.scheme)
                .appendEncodedPath("person_onboarding")
                .appendEncodedPath(uri.path.substring(1))
                .build()

        var apiStringUrl = apiUri.toString()
        if (apiStringUrl.last() != '/') {
            apiStringUrl = "$apiStringUrl/"
        }

        return apiStringUrl
    }

    fun clear() {
        instance?.stop()
        instance = null
    }

    const val LAST_COMPLETED_STEP_KEY = "LAST_COMPLETED_STEP_KEY"
    const val IDENTIFICATION_ID_KEY = "IDENTIFICATION_ID_KEY"
    const val SESSION_URL_KEY = "SESSION_URL_KEY"
}
