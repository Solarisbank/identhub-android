package de.solarisbank.identhub.session

import de.solarisbank.identhub.session.utils.buildApiUrl


object IdentHub {
    private var SESSION: IdentHubSession? = null

    val isPaymentResultAvailable: Boolean
        get() = SESSION?.isPaymentProcessAvailable ?: false

    fun sessionWithUrl(url: String): IdentHubSession {
        return SESSION ?: synchronized(this) {
            IdentHubSession(buildApiUrl(url)).apply {
                SESSION = this
            }
        }
    }

    fun clear() {
        SESSION?.stop()
        SESSION = null
    }

    const val IDENTIFICATION_ID_KEY = "IDENTIFICATION_ID_KEY"
    const val SESSION_URL_KEY = "SESSION_URL_KEY"
}
