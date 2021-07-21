package de.solarisbank.identhub.domain.session

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