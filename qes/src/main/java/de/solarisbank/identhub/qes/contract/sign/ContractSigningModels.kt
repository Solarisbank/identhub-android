package de.solarisbank.identhub.qes.contract.sign

import de.solarisbank.identhub.qes.data.dto.ContractSigningResult
import de.solarisbank.sdk.domain.model.result.Result

data class ContractSigningState(
    var phoneNumber: String?,
    var signingResult: Result<ContractSigningResult>?,
    var shouldShowResend: Boolean,
)

sealed class ContractSigningEvent {
    object CodeResent: ContractSigningEvent()
}

sealed class ContractSigningAction {
    object ResendCode: ContractSigningAction()
    object TimerExpired: ContractSigningAction()
    data class Submit(val code: String): ContractSigningAction()
}