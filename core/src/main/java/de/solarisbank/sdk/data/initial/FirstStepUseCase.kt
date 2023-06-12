package de.solarisbank.sdk.data.initial

import de.solarisbank.sdk.data.StartIdenthubConfig
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.domain.selectNextStep
import de.solarisbank.sdk.logger.IdLogger

interface FirstStepUseCase {
    suspend fun determineFirstStep(initialConfig: IdenthubInitialConfig): String
}

class FirstStepUseCaseImpl(
    private val identificationRepository: IdentificationRepository,
    private val startIdenthubConfig: StartIdenthubConfig,
): FirstStepUseCase {
    override suspend fun determineFirstStep(initialConfig: IdenthubInitialConfig): String {
        val defaultFirstStep = initialConfig.firstStep
        return if (startIdenthubConfig.identificationId != null) {
            try {
                val ident = identificationRepository.getRemoteIdentification(startIdenthubConfig.identificationId)
                identificationRepository.insertIdentification(ident)
                selectNextStep(ident.nextStep, ident.fallbackStep, initialConfig) ?: defaultFirstStep
            } catch (t: Throwable) {
                IdLogger.error("Couldn't determine first step: ${t.message}")
                defaultFirstStep
            }
        } else {
            defaultFirstStep
        }
    }
}