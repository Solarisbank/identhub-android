package de.solarisbank.identhub.data.session

interface SessionUrlLocalDataSource {
    fun get(): String?
    fun store(url: String?)
}