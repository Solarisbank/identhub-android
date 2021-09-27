package de.solarisbank.sdk.data.di.db

import de.solarisbank.sdk.data.dao.DocumentDao
import de.solarisbank.sdk.data.room.IdentityRoomDatabase
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

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