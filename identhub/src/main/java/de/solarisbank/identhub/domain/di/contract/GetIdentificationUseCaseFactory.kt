package de.solarisbank.identhub.domain.di.contract

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.identhub.domain.contract.GetIdentificationUseCase
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

class GetIdentificationUseCaseFactory(
    private val contractSignRepositoryProvider: Provider<ContractSignRepository>,
    private val identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
) : Factory<GetIdentificationUseCase> {
    override fun get(): GetIdentificationUseCase {
        return Preconditions.checkNotNull(
            GetIdentificationUseCase(
                contractSignRepositoryProvider.get(),
                identityInitializationRepositoryProvider.get()
            ),
            "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            contractSignRepositoryProvider: Provider<ContractSignRepository>,
            identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
        ): GetIdentificationUseCaseFactory {
            return GetIdentificationUseCaseFactory(
                contractSignRepositoryProvider,
                identityInitializationRepositoryProvider
            )
        }
    }
}