package de.solarisbank.sdk.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.solarisbank.sdk.data.dao.DocumentDao
import de.solarisbank.sdk.data.dao.IdentificationDao
import de.solarisbank.sdk.data.entity.Document
import de.solarisbank.sdk.data.entity.Identification
import de.solarisbank.sdk.data.utils.DateConverter

@Database(entities = [Document::class, Identification::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class IdentityRoomDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao

    abstract fun identificationDao(): IdentificationDao

    companion object {
        @Volatile
        private var INSTANCE: IdentityRoomDatabase? = null

        private const val DATABASE_NAME = "identity_database"
        fun getDatabase(context: Context): IdentityRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IdentityRoomDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        fun clearDatabase() {
            INSTANCE?.clearAllTables()
        }
    }
}