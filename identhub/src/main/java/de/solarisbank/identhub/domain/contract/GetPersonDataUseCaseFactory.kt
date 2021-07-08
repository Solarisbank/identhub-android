package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.session.data.identification.IdentificationRepository
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider

class GetPersonDataUseCaseFactory(
        private val identificationRepositoryProvider: Provider<IdentificationRepository>
) : Factory<GetMobileNumberUseCase> {
    override fun get(): GetMobileNumberUseCase {
        return Preconditions.checkNotNull(GetMobileNumberUseCase(identificationRepositoryProvider.get()))
    }

    companion object {
        @JvmStatic
        fun create(identificationRepositoryProvider: Provider<IdentificationRepository>): GetPersonDataUseCaseFactory {
            return GetPersonDataUseCaseFactory(identificationRepositoryProvider)
        }
    }
}