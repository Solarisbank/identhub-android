package de.solarisbank.identhub.data.contract.factory

import de.solarisbank.identhub.data.Mapper
import de.solarisbank.identhub.data.contract.ContractSignLocalDataSource
import de.solarisbank.identhub.data.contract.ContractSignModule
import de.solarisbank.identhub.data.contract.ContractSignNetworkDataSource
import de.solarisbank.identhub.domain.contract.ContractSignRepository
import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.data.entity.IdentificationWithDocument

class ProvideContractSignRepositoryFactory(
        private val contractSignModule: ContractSignModule,
        private val contractSignNetworkDataSourceProvider: Provider<ContractSignNetworkDataSource>,
        private val contractSignLocalDataSourceProvider: Provider<ContractSignLocalDataSource>,
        private val identificationEntityMapperProvider: Provider<Mapper<IdentificationDto, IdentificationWithDocument>>) : Factory<ContractSignRepository> {
    override fun get(): ContractSignRepository {
        return Preconditions.checkNotNull(
                contractSignModule.provideContractSignRepository(contractSignNetworkDataSourceProvider.get(), contractSignLocalDataSourceProvider.get(), identificationEntityMapperProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
                contractSignModule: ContractSignModule,
                contractSignNetworkDataSourceProvider: Provider<ContractSignNetworkDataSource>,
                contractSignLocalDataSourceProvider: Provider<ContractSignLocalDataSource>,
                identificationEntityMapperProvider: Provider<Mapper<IdentificationDto, IdentificationWithDocument>>): ProvideContractSignRepositoryFactory {
            return ProvideContractSignRepositoryFactory(contractSignModule, contractSignNetworkDataSourceProvider, contractSignLocalDataSourceProvider, identificationEntityMapperProvider)
        }
    }
}