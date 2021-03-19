package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.di.internal.Factory
import de.solarisbank.identhub.di.internal.Preconditions
import de.solarisbank.identhub.di.internal.Provider

class GetIdentificationUseCaseFactory(
        private val contractSignRepositoryProvider: Provider<ContractSignRepository>
) : Factory<GetIdentificationUseCase> {
    override fun get(): GetIdentificationUseCase {
        return Preconditions.checkNotNull(
                GetIdentificationUseCase(contractSignRepositoryProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(contractSignRepositoryProvider: Provider<ContractSignRepository>): GetIdentificationUseCaseFactory {
            return GetIdentificationUseCaseFactory(contractSignRepositoryProvider)
        }
    }
}