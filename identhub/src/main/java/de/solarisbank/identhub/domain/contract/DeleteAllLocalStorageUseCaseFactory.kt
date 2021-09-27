package de.solarisbank.identhub.domain.contract

import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

class DeleteAllLocalStorageUseCaseFactory(
        private val contractSignRepositoryProvider: Provider<ContractSignRepository>
) : Factory<DeleteAllLocalStorageUseCase> {
    override fun get(): DeleteAllLocalStorageUseCase {
        return Preconditions.checkNotNull(
                DeleteAllLocalStorageUseCase(contractSignRepositoryProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(contractSignRepositoryProvider: Provider<ContractSignRepository>): DeleteAllLocalStorageUseCaseFactory {
            return DeleteAllLocalStorageUseCaseFactory(contractSignRepositoryProvider)
        }
    }
}