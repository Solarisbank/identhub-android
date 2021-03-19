package de.solarisbank.identhub.data.session;

import de.solarisbank.identhub.domain.session.SessionUrlRepository;

public class SessionModule {

    public SessionUrlLocalDataSource provideSessionUrlLocalDataSource() {
        return new SessionUrlMemoryDataSource();
    }

    public SessionUrlRepository provideSessionUrlRepository(SessionUrlLocalDataSource sessionUrlLocalDataSource) {
        return new SessionUrlDataSourceRepository(sessionUrlLocalDataSource);
    }
}
