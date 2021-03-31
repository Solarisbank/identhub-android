package de.solarisbank.identhub.domain.contract

import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider

class GetDocumentsUseCaseFactory(
        private val contractSignRepositoryProvider: Provider<ContractSignRepository>
) : Factory<GetDocumentsUseCase> {
    override fun get(): GetDocumentsUseCase {
        return Preconditions.checkNotNull(
                GetDocumentsUseCase(contractSignRepositoryProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(contractSignRepositoryProvider: Provider<ContractSignRepository>): GetDocumentsUseCaseFactory {
            return GetDocumentsUseCaseFactory(contractSignRepositoryProvider)
        }
    }
}