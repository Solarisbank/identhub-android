package de.solarisbank.identhub.data.contract.factory

import de.solarisbank.identhub.data.contract.ContractSignLocalDataSource
import de.solarisbank.identhub.data.contract.ContractSignModule
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.data.dao.DocumentDao
import de.solarisbank.sdk.data.dao.IdentificationDao

class ContractSignLocalDataSourceFactory(
        private val contractSignModule: ContractSignModule,
        private val documentDaoProvider: Provider<DocumentDao>,
        private val identificationDaoProvider: Provider<IdentificationDao>
) : Factory<ContractSignLocalDataSource?> {
    override fun get(): ContractSignLocalDataSource {
        return Preconditions.checkNotNull(
                contractSignModule.provideContractSignLocalDataSource(documentDaoProvider.get(), identificationDaoProvider.get()),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
                contractSignModule: ContractSignModule,
                documentDaoProvider: Provider<DocumentDao>,
                identificationDaoProvider: Provider<IdentificationDao>
        ): ContractSignLocalDataSourceFactory {
            return ContractSignLocalDataSourceFactory(contractSignModule, documentDaoProvider, identificationDaoProvider)
        }
    }
}