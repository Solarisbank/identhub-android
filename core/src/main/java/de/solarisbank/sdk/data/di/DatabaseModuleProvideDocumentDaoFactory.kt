package de.solarisbank.sdk.data.di

import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.data.dao.DocumentDao
import de.solarisbank.sdk.data.room.IdentityRoomDatabase

class DatabaseModuleProvideDocumentDaoFactory(
    private val databaseModule: DatabaseModule,
    private val identityRoomDatabaseProvider: Provider<IdentityRoomDatabase>
) : Factory<DocumentDao> {
    override fun get(): DocumentDao {
        return Preconditions.checkNotNull(
                databaseModule.provideDocumentDao(identityRoomDatabaseProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            databaseModule: DatabaseModule,
            identityRoomDatabaseProvider: Provider<IdentityRoomDatabase>
        ): DatabaseModuleProvideDocumentDaoFactory {
            return DatabaseModuleProvideDocumentDaoFactory(
                    databaseModule,
                    identityRoomDatabaseProvider
            )
        }
    }
}