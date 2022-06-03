package de.solarisbank.identhub.session

import de.solarisbank.identhub.session.feature.IdentHubSession
import de.solarisbank.identhub.session.feature.utils.buildApiUrl
import timber.log.Timber

class IdentHub {
    init {
        Timber.d("init, this: $this")
    }

    private var SESSION: IdentHubSession? = null

    @Synchronized
    fun sessionWithUrl(url: String): IdentHubSession {
        val apiUrl = buildApiUrl(url)
        SESSION?.sessionUrl = apiUrl
        return SESSION ?: IdentHubSession().apply {
            sessionUrl = apiUrl
            SESSION = this
        }
    }

    @Synchronized
    fun clear() {
        Timber.d("clear(), this: $this")
        SESSION?.reset()
    }

    companion object {
        const val IDENTIFICATION_ID_KEY = "IDENTIFICATION_ID_KEY"
        const val SESSION_URL_KEY = "SESSION_URL_KEY"
        const val VERIFICATION_BANK_URL_KEY = "VERIFICATION_BANK_URL_KEY"
    }
}
