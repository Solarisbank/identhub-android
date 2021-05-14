package de.solarisbank.identhub.session

import de.solarisbank.identhub.session.utils.buildApiUrl


object IdentHub {
    private var instance: IdentHubSession? = null

    val isPaymentResultAvailable: Boolean
        get() = instance?.isPaymentProcessAvailable ?: false

    fun sessionWithUrl(url: String): IdentHubSession {
        return IdentHubSession(buildApiUrl(url)).apply { instance = this }
    }

    fun clear() {
        instance?.stop()
        instance = null
    }

    const val LAST_COMPLETED_STEP_KEY = "LAST_COMPLETED_STEP_KEY"
    const val IDENTIFICATION_ID_KEY = "IDENTIFICATION_ID_KEY"
    const val SESSION_URL_KEY = "SESSION_URL_KEY"
}
