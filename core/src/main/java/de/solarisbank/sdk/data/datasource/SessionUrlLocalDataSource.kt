package de.solarisbank.sdk.data.datasource

interface SessionUrlLocalDataSource {
    fun get(): String?
    fun store(url: String?)
}