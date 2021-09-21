package de.solarisbank.sdk.data.dao

import androidx.room.*
import de.solarisbank.sdk.data.entity.Document
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(document: List<Document>): Completable

    @Query("SELECT * FROM ${Document.TABLE_NAME}")
    fun getAll(): Single<List<Document>>

    @Update
    fun update(document: Document)

    @Delete
    fun delete(document: Document): Completable

    @Query("DELETE FROM ${Document.TABLE_NAME}")
    fun deleteAll(): Completable
}