package de.solarisbank.identhub.session.data.contract

import de.solarisbank.identhub.domain.contract.ContractSignRepository
import de.solarisbank.identhub.session.data.Mapper
import de.solarisbank.sdk.data.dao.DocumentDao
import de.solarisbank.sdk.data.dao.IdentificationDao
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.IdentificationWithDocument
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