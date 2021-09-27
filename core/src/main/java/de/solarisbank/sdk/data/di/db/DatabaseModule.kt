package de.solarisbank.sdk.data.di.db

import android.content.Context
import de.solarisbank.sdk.data.dao.DocumentDao
import de.solarisbank.sdk.data.dao.IdentificationDao
import de.solarisbank.sdk.data.room.IdentityRoomDatabase
import de.solarisbank.sdk.data.room.IdentityRoomDatabase.Companion.getDatabase

class DatabaseModule {
    fun provideIdentityRoomDatabase(context: Context): IdentityRoomDatabase {
        return getDatabase(context)
    }

    fun provideDocumentDao(identityRoomDatabase: IdentityRoomDatabase): DocumentDao {
        return identityRoomDatabase.documentDao()
    }

    fun provideIdentificationDao(identityRoomDatabase: IdentityRoomDatabase): IdentificationDao {
        return identityRoomDatabase.identificationDao()
    }
}