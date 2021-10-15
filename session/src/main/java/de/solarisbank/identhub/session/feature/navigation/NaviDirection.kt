package de.solarisbank.identhub.session.feature.navigation

import android.os.Bundle
import de.solarisbank.identhub.session.feature.IdentHubSessionResult
import de.solarisbank.identhub.session.feature.navigation.router.COMPLETED_STEP

sealed class NaviDirection {
    class FragmentDirection(
        val actionId: Int,
        val args: Bundle? = null
    ) : NaviDirection()

    class PaymentSuccessfulStepResult(identificationId: String): NaviDirection(), SessionStepResult {
        val result: IdentHubSessionResult =
            IdentHubSessionResult(identificationId, COMPLETED_STEP.VERIFICATION_BANK)
    }

    class NextStepStepResult(val nextStep: String?, val completedStep: Int? = null): NaviDirection(),
        SessionStepResult

    class VerificationSuccessfulStepResult(
        val identificationId: String,
        val completedStep: Int
    ): NaviDirection(), SessionStepResult

    class VerificationFailureStepResult(val completedStep: Int? = null): NaviDirection(), SessionStepResult
}