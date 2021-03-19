package de.solarisbank.identhub.di.database

import android.content.Context
import de.solarisbank.identhub.data.dao.DocumentDao
import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.data.room.IdentityRoomDatabase
import de.solarisbank.identhub.data.room.IdentityRoomDatabase.Companion.getDatabase

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