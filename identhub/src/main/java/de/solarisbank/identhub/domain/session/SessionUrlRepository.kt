package de.solarisbank.identhub.domain.session

interface SessionUrlRepository {
    fun get(): String?
    fun save(url: String?)
}