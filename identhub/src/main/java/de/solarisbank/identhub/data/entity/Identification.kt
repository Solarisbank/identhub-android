package de.solarisbank.identhub.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.solarisbank.identhub.data.entity.Identification.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Identification(
        @PrimaryKey
        var id: String,
        var url: String,
        var status: String,
        var nextStep: String? = null) {

    val isCompleted: Boolean
        get() = Status.CONFIRMED == Status.getEnum(status)

    val isAuthorizationRequiredOrFailed: Boolean
        get() {
            val currentStatus = Status.getEnum(status)
            return Status.AUTHORIZATION_REQUIRED == currentStatus || Status.FAILED == currentStatus
        }


    companion object {
        const val TABLE_NAME = "identification_table"
        const val VERIFICATION_BANK_URL_KEY = "VERIFICATION_BANK_URL_KEY"
    }
}
