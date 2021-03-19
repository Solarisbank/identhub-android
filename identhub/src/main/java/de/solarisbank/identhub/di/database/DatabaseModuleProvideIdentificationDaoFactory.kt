package de.solarisbank.identhub.di.database

import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.data.room.IdentityRoomDatabase
import de.solarisbank.identhub.di.internal.Factory
import de.solarisbank.identhub.di.internal.Preconditions
import de.solarisbank.identhub.di.internal.Provider

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