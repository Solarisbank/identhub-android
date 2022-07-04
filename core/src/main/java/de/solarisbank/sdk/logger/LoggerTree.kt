package de.solarisbank.sdk.logger

import timber.log.Timber

class LoggerTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        IdLogger.logInfo(message, "TimberLog")
    }
}