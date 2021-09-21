package de.solarisbank.sdk.data.dao

import androidx.room.*
import de.solarisbank.sdk.data.entity.Identification
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface IdentificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(identification: Identification): Completable

    @Query("SELECT * FROM ${Identification.TABLE_NAME}")
    fun getAll(): Single<List<Identification>>

    @Update
    fun update(identification: Identification)

    @Delete
    fun delete(identification: Identification): Completable

    @Query("DELETE FROM ${Identification.TABLE_NAME}")
    fun deleteAll(): Completable

    @Query("SELECT * FROM ${Identification.TABLE_NAME} LIMIT 1")
    fun get(): Single<Identification>
}