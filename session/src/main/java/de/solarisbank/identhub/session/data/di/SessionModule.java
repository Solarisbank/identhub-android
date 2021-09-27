package de.solarisbank.identhub.session.data.di;

import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource;
import de.solarisbank.sdk.data.di.datasource.SessionUrlMemoryDataSource;
import de.solarisbank.sdk.data.repository.SessionUrlDataSourceRepository;
import de.solarisbank.sdk.data.repository.SessionUrlRepository;

public class SessionModule {

    public SessionUrlLocalDataSource provideSessionUrlLocalDataSource() {
        return new SessionUrlMemoryDataSource();
    }

    public SessionUrlRepository provideSessionUrlRepository(SessionUrlLocalDataSource sessionUrlLocalDataSource) {
        return new SessionUrlDataSourceRepository(sessionUrlLocalDataSource);
    }
}
