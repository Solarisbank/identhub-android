package de.solarisbank.sdk.data.di.datasource

import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource

class SessionUrlMemoryDataSource : SessionUrlLocalDataSource {
    private var url: String? = null

    override fun get(): String? {
        return url
    }

    override fun store(url: String?) {
        this.url = url
    }
}