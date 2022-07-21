package de.solarisbank.sdk.domain

import android.annotation.SuppressLint
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import timber.log.Timber

/**
 * This class selects the next step based on partner settings.
 */

interface NextStepSelector {

    val identityInitializationRepository: IdentityInitializationRepository

    @SuppressLint("BinaryOperationInTimber")
    fun selectNextStep(nextStep: String?, fallbackStep: String?): String? {
        val defaultToFallbackStep =
            identityInitializationRepository
                .getInitializationDto()
                ?.partnerSettings
                ?.defaultToFallbackStep
        Timber.d(
            "selectNextStep, nextStep : $nextStep," +
                    " fallbackStep : $fallbackStep," +
                    " defaultToFallbackStep : $defaultToFallbackStep"
        )
        return if (defaultToFallbackStep == true) {
            fallbackStep ?: nextStep
        } else {
            nextStep
        }
    }
}