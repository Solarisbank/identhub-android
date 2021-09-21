package de.solarisbank.sdk.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = Document.TABLE_NAME)
data class Document(
        @PrimaryKey
        val id: String,
        val name: String,
        val contentType: String,
        val documentType: String,
        val size: Long,
        val isCustomerAccessible: Boolean,
        val createdAt: Date?,
        val deletedAt: Date?,
        var identificationId: String) {

    companion object {
        const val TABLE_NAME = "document_table"
    }
}