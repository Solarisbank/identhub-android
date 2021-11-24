package de.solarisbank.sdk.fourthline.data.entity

import android.content.Context
import com.fourthline.core.DocumentType
import java.io.Serializable

/**
 * Represents an enum list of documents that could be used for fourthline identification passing
 */
enum class AppliedDocument(val isSupported: Boolean = true) : Serializable {
    PASSPORT,
    NATIONAL_ID_CARD,
    PAPER_ID,
    FRENCH_ID_CARD(false),
    RESIDENCE_PERMIT(false),
    DRIVING_LICENSE(false),
    DUTCH_DRIVERS_LICENCE(false)
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

fun AppliedDocument.asString(context: Context) = when (this) {
    AppliedDocument.PASSPORT -> context.resources.getString(de.solarisbank.sdk.fourthline.R.string.fourthline_doc_type_passport)
    AppliedDocument.NATIONAL_ID_CARD -> context.resources.getString(de.solarisbank.sdk.fourthline.R.string.fourthline_doc_type_id_card)
    AppliedDocument.FRENCH_ID_CARD -> context.resources.getString(de.solarisbank.sdk.fourthline.R.string.fourthline_doc_type_french_id_card)
    AppliedDocument.PAPER_ID -> context.resources.getString(de.solarisbank.sdk.fourthline.R.string.fourthline_doc_type_paper_id)
    AppliedDocument.RESIDENCE_PERMIT -> context.resources.getString(de.solarisbank.sdk.fourthline.R.string.fourthline_doc_type_residence_permit)
    AppliedDocument.DRIVING_LICENSE -> context.resources.getString(de.solarisbank.sdk.fourthline.R.string.fourthline_doc_type_driving_licence)
    AppliedDocument.DUTCH_DRIVERS_LICENCE -> context.resources.getString(de.solarisbank.sdk.fourthline.R.string.fourthline_doc_type_drivers_licence)
}