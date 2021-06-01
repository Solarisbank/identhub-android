package de.solarisbank.identhub.session.domain

import de.solarisbank.identhub.session.data.identification.IdentificationRepository
import de.solarisbank.sdk.core.di.internal.Factory

class IdentificationPollingStatusUseCaseFactory private constructor(
        private val identificationStatusRepository: IdentificationRepository
) : Factory<IdentificationPollingStatusUseCase> {

    override fun get(): IdentificationPollingStatusUseCase {
        return IdentificationPollingStatusUseCase(identificationStatusRepository)
    }

    companion object {
        @JvmStatic
        fun create(identificationStatusRepository: IdentificationRepository): IdentificationPollingStatusUseCaseFactory {
            return IdentificationPollingStatusUseCaseFactory(identificationStatusRepository)
        }
    }
}