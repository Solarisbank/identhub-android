package de.solarisbank.sdk.domain

import de.solarisbank.sdk.data.repository.IdentityInitializationRepository

/**
 * This class selects the next step based on partner settings.
 */

interface NextStepSelector {
    val identityInitializationRepository: IdentityInitializationRepository

    fun selectNextStep(nextStep: String?, fallbackStep: String?): String? {
        val defaultToFallbackStep = identityInitializationRepository.getInitializationDto()?.partnerSettings?.defaultToFallbackStep
        return if (defaultToFallbackStep == true) {
            fallbackStep ?: nextStep
        } else {
            nextStep
        }
    }
}