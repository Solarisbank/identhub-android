package de.solarisbank.identhub.session


object IdentHub {
    private var instance: IdentHubSession? = null

    val isPaymentResultAvailable: Boolean
        get() = instance?.isPaymentProcessAvailable ?: false

    fun sessionWithUrl(url: String): IdentHubSession {
        return IdentHubSession(url).apply { instance = this }
    }

    fun currentSession(): IdentHubSession {
        return instance ?: throw IllegalStateException("Session is not initialized")
    }

    fun clear() {
        instance?.stop()
        instance = null
    }

    const val LAST_COMPLETED_STEP_KEY = "LAST_COMPLETED_STEP_KEY"
    const val IDENTIFICATION_ID_KEY = "IDENTIFICATION_ID_KEY"
    const val SESSION_URL_KEY = "SESSION_TOKEN_KEY"
}
