package de.solarisbank.identhub.data.session

import de.solarisbank.identhub.domain.session.SessionUrlRepository

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