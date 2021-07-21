package de.solarisbank.identhub.session.domain

import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.identhub.session.data.identification.IdentificationRepository
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Provider

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
