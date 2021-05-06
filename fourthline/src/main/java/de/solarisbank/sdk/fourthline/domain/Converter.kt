package de.solarisbank.sdk.fourthline.domain

import de.solarisbank.sdk.fourthline.data.dto.PersonDataDto
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument

fun PersonDataDto.appliedDocuments(): List<AppliedDocument>? {
    return this.supportedDocuments
            ?.map { it?.type?.appliedDocument() }
            ?.filterNotNull()
            ?.toList()
}

private fun String.appliedDocument(): AppliedDocument {
    return if (contains("pas", true)) {
    AppliedDocument.PASSPORT
    } else {
        AppliedDocument.ID_CARD
    }
}