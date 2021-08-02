package de.solarisbank.sdk.fourthline.domain.dto

import java.net.URI

sealed class ZipCreationStateDto {
    //todo describe all possible errors
    data class SUCCESS(val uri: URI) : ZipCreationStateDto()
    object ERROR : ZipCreationStateDto()
}