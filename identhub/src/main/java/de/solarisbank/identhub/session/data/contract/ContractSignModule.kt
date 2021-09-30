package de.solarisbank.identhub.session.data.contract

import de.solarisbank.identhub.domain.contract.ContractSignRepository
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import retrofit2.Retrofit

class ContractSignModule {
    fun provideContractSignApi(retrofit: Retrofit): ContractSignApi {
        return retrofit.create(ContractSignApi::class.java)
    }

    fun provideContractSignNetworkDataSource(contractSignApi: ContractSignApi): ContractSignNetworkDataSource {
        return ContractSignRetrofitDataSource(contractSignApi)
    }

    fun provideContractSignRepository(
            contractSignNetworkDataSource: ContractSignNetworkDataSource,
            identificationLocalDataSource: IdentificationLocalDataSource,
            ): ContractSignRepository {
        return ContractSignDataSourceRepository(contractSignNetworkDataSource, identificationLocalDataSource)
    }
}