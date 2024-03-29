package de.solarisbank.sdk.data.repository

import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource

class SessionUrlDataSourceRepository(
        private val sessionUrlLocalDataSource: SessionUrlLocalDataSource
) : SessionUrlRepository {
    override fun get(): String? {
        return sessionUrlLocalDataSource.get()
    }

    override fun save(url: String?) {
        sessionUrlLocalDataSource.store(url)
    }
}