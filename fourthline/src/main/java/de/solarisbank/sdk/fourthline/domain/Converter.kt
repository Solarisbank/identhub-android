package de.solarisbank.sdk.fourthline.domain

import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument.NATIONAL_ID_CARD
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument.valueOf

fun PersonDataDto.appliedDocuments(): List<AppliedDocument>? {
    return this.supportedDocuments
            ?.filter { !(it?.issuingCountries?.isEmpty() ?: true) }
            ?.mapNotNull { it?.type?.appliedDocument() }
            ?.filter { it.isSupported }
            ?.toList()
}

private fun String.appliedDocument(): AppliedDocument {
    return try {
        valueOf(replace(" ", "_").toUpperCase())
    } catch (e: IllegalArgumentException) {
        NATIONAL_ID_CARD
    }
}