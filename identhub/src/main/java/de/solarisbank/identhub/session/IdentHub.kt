package de.solarisbank.identhub.session

import de.solarisbank.identhub.session.utils.buildApiUrl
import timber.log.Timber


object IdentHub {
    init {
        Timber.d("init, this: $this")
    }
    private var SESSION: IdentHubSession? = null

    @Synchronized
    fun isPaymentResultAvailable(): Boolean {
        return SESSION?.isPaymentProcessAvailable ?: false
    }

    @Synchronized
    fun sessionWithUrl(url: String): IdentHubSession {
        val apiUrl = buildApiUrl(url)
        if (SESSION != null && SESSION?.sessionUrlString != apiUrl) {
            clearSession()
        }
        return SESSION ?: IdentHubSession(apiUrl).apply {
            SESSION = this
        }
    }

    @Synchronized
    fun clear() {
        Timber.d("clear(), this: $this")
        clearSession()
    }

    private fun clearSession() {
        SESSION?.stop()
        SESSION = null
    }

    const val IDENTIFICATION_ID_KEY = "IDENTIFICATION_ID_KEY"
    const val SESSION_URL_KEY = "SESSION_URL_KEY"
}
