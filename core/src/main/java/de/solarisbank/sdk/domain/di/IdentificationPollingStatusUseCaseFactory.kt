package de.solarisbank.sdk.domain.di

import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.di.internal.Factory

class IdentificationPollingStatusUseCaseFactory private constructor(
    private val identificationStatusRepository: IdentificationRepository,
    private val identityInitializationRepository: IdentityInitializationRepository
) : Factory<IdentificationPollingStatusUseCase> {

    override fun get(): IdentificationPollingStatusUseCase {
        return IdentificationPollingStatusUseCase(
            identificationStatusRepository,
            identityInitializationRepository
        )
    }

    companion object {
        @JvmStatic
        fun create(
            identificationStatusRepository: IdentificationRepository,
            identityInitializationRepository: IdentityInitializationRepository
        ): IdentificationPollingStatusUseCaseFactory {
            return IdentificationPollingStatusUseCaseFactory(
                identificationStatusRepository,
                identityInitializationRepository
            )
        }
    }
}
