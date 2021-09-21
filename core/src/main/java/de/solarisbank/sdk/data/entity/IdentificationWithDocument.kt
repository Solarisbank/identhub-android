package de.solarisbank.sdk.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class IdentificationWithDocument(@Embedded val identification: Identification,
                                      @Relation(parentColumn = "id", entityColumn = "identificationId")
                                      val documents: List<Document>)