package de.solarisbank.identhub.session

import de.solarisbank.identhub.session.feature.IdentHubSession
import de.solarisbank.identhub.session.feature.di.IdentHubSessionReceiver
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.identhub.session.feature.utils.buildApiUrl
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.feature.di.internal.Provider
import timber.log.Timber

object IdentHub : IdentHubSessionReceiver {
    init {
        Timber.d("init, this: $this")
    }

    private var SESSION: IdentHubSession? = null

    fun getIdentificationLocalDataSourceProvider(): Provider<IdentificationLocalDataSource> {
        return SESSION!!.getIdentificationLocalDataSourceProvider()
    }

    @Synchronized
    fun isPaymentResultAvailable(): Boolean {
        return SESSION?.isPaymentProcessAvailable ?: false
    }

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

    const val IDENTIFICATION_ID_KEY = "IDENTIFICATION_ID_KEY"
    const val SESSION_URL_KEY = "SESSION_URL_KEY"
    const val VERIFICATION_BANK_URL_KEY = "VERIFICATION_BANK_URL_KEY"

    override fun setSessionResult(sessionStepResult: SessionStepResult) {
        SESSION!!.setSessionResult(sessionStepResult)
    }
}
