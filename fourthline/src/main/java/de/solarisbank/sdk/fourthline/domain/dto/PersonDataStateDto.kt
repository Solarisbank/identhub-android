package de.solarisbank.sdk.fourthline.domain.dto

import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument

sealed class PersonDataStateDto {
    object UPLOADING : PersonDataStateDto()
    class SUCCEEDED(val docs: List<AppliedDocument>) : PersonDataStateDto()
    object EMPTY_DOCS_LIST_ERROR: PersonDataStateDto()
    object GENERIC_ERROR : PersonDataStateDto()
}