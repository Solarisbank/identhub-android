package de.solarisbank.identhub.domain.data.dto

sealed class ContractSigningState {
    data class SUCCESSFUL(val identificationId: String) : ContractSigningState()
    data class CONFIRMED (val identificationId: String) : ContractSigningState()
    data class FAILED (val identificationId: String) : ContractSigningState()
    object GENERIC_ERROR: ContractSigningState()
}