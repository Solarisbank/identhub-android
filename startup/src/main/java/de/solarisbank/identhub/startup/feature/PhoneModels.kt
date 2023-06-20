package de.solarisbank.identhub.startup.feature

import de.solarisbank.sdk.domain.model.result.Result

sealed class PhoneVerificationAction {
    object ResendCode: PhoneVerificationAction()
    object TimerExpired: PhoneVerificationAction()
    data class CodeChanged(val code: String): PhoneVerificationAction()
    data class Submit(val code: String?): PhoneVerificationAction()
}

data class PhoneVerificationState(
    var phoneNumber: String? = null,
    var verifyResult: Result<Unit>? = null,
    var shouldShowResend: Boolean = false,
    var submitEnabled: Boolean = false
)

sealed class PhoneVerificationEvent {
    object CodeResent: PhoneVerificationEvent()
}