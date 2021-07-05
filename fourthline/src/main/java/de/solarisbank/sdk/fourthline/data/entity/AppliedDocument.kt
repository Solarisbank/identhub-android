package de.solarisbank.sdk.fourthline.data.entity

import com.fourthline.core.DocumentType
import java.io.Serializable

/**
 * Represents an enum list of documents that could be used for fourthline identification passing
 */
enum class AppliedDocument(val displayString: String, val isSupported: Boolean = true) : Serializable {
    PASSPORT("Passport"),
    NATIONAL_ID_CARD("ID Card"),
    FRENCH_ID_CARD("French ID Card", false),
    PAPER_ID("Paper ID"),
    RESIDENCE_PERMIT("Residence Permit", false),
    DRIVING_LICENSE("Drivers Licence", false),
    DUTCH_DRIVERS_LICENCE("Driving Licence", false)
}

fun AppliedDocument.toDocumentType(): DocumentType {
    return when (this) {
        AppliedDocument.PASSPORT -> DocumentType.PASSPORT
        AppliedDocument.NATIONAL_ID_CARD -> DocumentType.ID_CARD
        AppliedDocument.FRENCH_ID_CARD -> DocumentType.FRENCH_ID_CARD
        AppliedDocument.PAPER_ID -> DocumentType.PAPER_ID
        AppliedDocument.RESIDENCE_PERMIT -> DocumentType.RESIDENCE_PERMIT
        AppliedDocument.DRIVING_LICENSE -> DocumentType.DRIVERS_LICENSE
        AppliedDocument.DUTCH_DRIVERS_LICENCE -> DocumentType.DUTCH_DRIVERS_LICENSE
    }
}