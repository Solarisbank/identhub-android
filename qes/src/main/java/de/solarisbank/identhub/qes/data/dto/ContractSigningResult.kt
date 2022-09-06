package de.solarisbank.identhub.qes.data.dto

sealed class ContractSigningResult {
    data class Successful(val identificationId: String) : ContractSigningResult()
    data class Confirmed (val identificationId: String) : ContractSigningResult()
    data class Failed (val identificationId: String) : ContractSigningResult()
    object GenericError: ContractSigningResult()
}