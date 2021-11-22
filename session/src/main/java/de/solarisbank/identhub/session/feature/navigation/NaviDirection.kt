package de.solarisbank.identhub.session.feature.navigation

import android.os.Bundle
import de.solarisbank.identhub.session.feature.navigation.router.COMPLETED_STEP

sealed class NaviDirection {

    class FragmentDirection(
        val actionId: Int,
        val args: Bundle? = null
    ) : NaviDirection()

    class PaymentSuccessfulStepResult(
        val identificationId: String,
        val completedStep: Int = COMPLETED_STEP.VERIFICATION_BANK.index
    ): NaviDirection(), SessionStepResult

    class NextStepStepResult(
        val nextStep: String?,
        val completedStep: Int? = null
    ): NaviDirection(), SessionStepResult

    class ConfirmationSuccessfulStepResult(
        val identificationId: String,
        val completedStep: Int = COMPLETED_STEP.CONTRACT_SIGNING.index
    ): NaviDirection(), SessionStepResult

    class VerificationSuccessfulStepResult(
        val identificationId: String,
        val completedStep: Int
    ): NaviDirection(), SessionStepResult

    class VerificationFailureStepResult(
        val completedStep: Int? = null
    ): NaviDirection(), SessionStepResult
}