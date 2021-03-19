package de.solarisbank.identhub.data.contract

import de.solarisbank.identhub.data.Mapper
import de.solarisbank.identhub.data.dao.DocumentDao
import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.entity.IdentificationWithDocument
import de.solarisbank.identhub.domain.contract.ContractSignRepository
import retrofit2.Retrofit

class ContractSignModule {
    fun provideContractSignApi(retrofit: Retrofit): ContractSignApi {
        return retrofit.create(ContractSignApi::class.java)
    }

    fun provideContractSignNetworkDataSource(contractSignApi: ContractSignApi): ContractSignNetworkDataSource {
        return ContractSignRetrofitDataSource(contractSignApi)
    }

    fun provideContractSignLocalDataSource(documentDao: DocumentDao, identificationDao: IdentificationDao): ContractSignLocalDataSource {
        return ContractSignRoomDataSource(documentDao, identificationDao)
    }

    fun provideContractSignRepository(
            contractSignNetworkDataSource: ContractSignNetworkDataSource,
            contractSignLocalDataSource: ContractSignLocalDataSource,
            identificationEntityMapper: Mapper<IdentificationDto, IdentificationWithDocument>): ContractSignRepository {
        return ContractSignDataSourceRepository(contractSignNetworkDataSource, contractSignLocalDataSource, identificationEntityMapper)
    }
}