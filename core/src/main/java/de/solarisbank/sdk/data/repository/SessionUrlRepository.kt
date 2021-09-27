package de.solarisbank.sdk.data.repository

interface SessionUrlRepository {
    fun get(): String?
    fun save(url: String?)
}