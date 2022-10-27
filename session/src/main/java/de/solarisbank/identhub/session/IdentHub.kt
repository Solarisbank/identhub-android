package de.solarisbank.identhub.session

import android.app.Activity
import android.content.Intent
import de.solarisbank.identhub.session.feature.IdentHubSession
import de.solarisbank.identhub.session.feature.utils.buildApiUrl
import de.solarisbank.identhub.session.main.MainActivity
import de.solarisbank.sdk.logger.IdLogger
import timber.log.Timber

class IdentHub {
    init {
        Timber.d("init, this: $this")
    }

    private var SESSION: IdentHubSession? = null

    /**
     * optional enableLocalLogger field to enable local logging of SDK. Will be disabled by default.
     */
    @Synchronized
    fun sessionWithUrl(url: String): IdentHubSession {
        val apiUrl = buildApiUrl(url)
        SESSION?.sessionUrl = apiUrl

        IdLogger.setupLogger()

        return SESSION ?: IdentHubSession().apply {
            sessionUrl = apiUrl
            SESSION = this
        }
    }

    fun startForResult(activity: Activity, requestCode: Int, sessionUrl: String) {
        val intent = Intent(activity, MainActivity::class.java)
            .putExtra("session_url", sessionUrl)
        activity.startActivityForResult(intent, requestCode)
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
