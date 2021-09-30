package de.solarisbank.identhub.session.data.contract.factory

import de.solarisbank.identhub.domain.contract.ContractSignRepository
import de.solarisbank.identhub.session.data.contract.ContractSignModule
import de.solarisbank.identhub.session.data.contract.ContractSignNetworkDataSource
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

class ProvideContractSignRepositoryFactory(
    private val contractSignModule: ContractSignModule,
    private val contractSignNetworkDataSourceProvider: Provider<ContractSignNetworkDataSource>,
    private val identificationLocalDataSource: Provider<out IdentificationLocalDataSource>,
) : Factory<ContractSignRepository> {
    override fun get(): ContractSignRepository {
        return Preconditions.checkNotNull(
                contractSignModule.provideContractSignRepository(
                    contractSignNetworkDataSourceProvider.get(), identificationLocalDataSource.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            contractSignModule: ContractSignModule,
            contractSignNetworkDataSourceProvider: Provider<ContractSignNetworkDataSource>,
            identificationLocalDataSource: Provider<out IdentificationLocalDataSource>,
        ): ProvideContractSignRepositoryFactory {
            return ProvideContractSignRepositoryFactory(
                contractSignModule,
                contractSignNetworkDataSourceProvider,
                identificationLocalDataSource
            )
        }
    }
}