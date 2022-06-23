package de.solarisbank.sdk.logger.domain.model


class LogType {
    companion object {
        const val info = "INFO"
        const val event = "EVENT"
        const val action = "ACTION"
        const val nav = "NAV"
        const val error = "ERROR"
        const val fault = "FAULT"
    }
}