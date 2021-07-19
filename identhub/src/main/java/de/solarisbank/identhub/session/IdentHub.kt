package de.solarisbank.identhub.session

import de.solarisbank.identhub.session.utils.buildApiUrl
import timber.log.Timber


object IdentHub {
    init {
        Timber.d("init, this: $this")
    }
    private var SESSION: IdentHubSession? = null

    val isPaymentResultAvailable: Boolean
        get() = SESSION?.isPaymentProcessAvailable ?: false

    fun sessionWithUrl(url: String): IdentHubSession {
        synchronized(this) {
            if (!SESSION?.sessionUrl.equals(url)) {
                clear()
            }
            return SESSION ?: IdentHubSession(buildApiUrl(url)).apply {
                SESSION = this
            }
        }
    }

    fun clear() {
        Timber.d("clear(), this: $this")
        SESSION?.stop()
        SESSION = null
    }

    const val IDENTIFICATION_ID_KEY = "IDENTIFICATION_ID_KEY"
    const val SESSION_URL_KEY = "SESSION_URL_KEY"
}
