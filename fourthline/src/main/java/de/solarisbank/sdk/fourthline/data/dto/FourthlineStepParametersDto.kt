package de.solarisbank.sdk.fourthline.data.dto

data class FourthlineStepParametersDto(
    var isFourthlineSigning: Boolean,
    var createIdentificationOnRetry: Boolean,
    var showUploadingScreen: Boolean,
    var showStepIndicator: Boolean
)