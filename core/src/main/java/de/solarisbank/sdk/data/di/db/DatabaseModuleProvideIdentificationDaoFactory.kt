package de.solarisbank.sdk.data.di.db

import de.solarisbank.sdk.data.dao.IdentificationDao
import de.solarisbank.sdk.data.room.IdentityRoomDatabase
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

class DatabaseModuleProvideIdentificationDaoFactory(
    private val databaseModule: DatabaseModule,
    private val identityRoomDatabaseProvider: Provider<IdentityRoomDatabase>
) : Factory<IdentificationDao> {
    override fun get(): IdentificationDao {
        return Preconditions.checkNotNull(
                databaseModule.provideIdentificationDao(identityRoomDatabaseProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            databaseModule: DatabaseModule,
            identityRoomDatabaseProvider: Provider<IdentityRoomDatabase>
        ): DatabaseModuleProvideIdentificationDaoFactory {
            return DatabaseModuleProvideIdentificationDaoFactory(
                    databaseModule,
                    identityRoomDatabaseProvider
            )
        }
    }
}