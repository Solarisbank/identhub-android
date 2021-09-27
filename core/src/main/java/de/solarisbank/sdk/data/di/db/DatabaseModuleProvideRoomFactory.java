package de.solarisbank.sdk.data.di.db;

import android.content.Context;

import de.solarisbank.sdk.data.room.IdentityRoomDatabase;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;

public final class DatabaseModuleProvideRoomFactory implements Factory<IdentityRoomDatabase> {

    private final DatabaseModule databaseModule;
    private final Provider<Context> contextProvider;

    public DatabaseModuleProvideRoomFactory(
            DatabaseModule databaseModule,
            Provider<Context> contextProvider) {
        this.databaseModule = databaseModule;
        this.contextProvider = contextProvider;
    }

    public static DatabaseModuleProvideRoomFactory create(
            DatabaseModule databaseModule,
            Provider<Context> contextProvider
    ) {
        return new DatabaseModuleProvideRoomFactory(
                databaseModule,
                contextProvider
        );
    }

    @Override
    public IdentityRoomDatabase get() {
        return Preconditions.checkNotNull(
                databaseModule.provideIdentityRoomDatabase(contextProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
