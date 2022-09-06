package de.solarisbank.identhub.domain.di.contract

import de.solarisbank.identhub.data.contract.step.parameters.GetMobileNumberUseCase
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

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