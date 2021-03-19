package de.solarisbank.identhub.data.contract.factory

import de.solarisbank.identhub.data.Mapper
import de.solarisbank.identhub.data.contract.ContractSignLocalDataSource
import de.solarisbank.identhub.data.contract.ContractSignModule
import de.solarisbank.identhub.data.contract.ContractSignNetworkDataSource
import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.entity.IdentificationWithDocument
import de.solarisbank.identhub.di.internal.Factory
import de.solarisbank.identhub.di.internal.Preconditions
import de.solarisbank.identhub.di.internal.Provider
import de.solarisbank.identhub.domain.contract.ContractSignRepository

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