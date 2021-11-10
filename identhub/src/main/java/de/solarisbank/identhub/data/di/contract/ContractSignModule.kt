package de.solarisbank.identhub.data.di.contract

import de.solarisbank.identhub.data.contract.*
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider
import retrofit2.Retrofit

class ContractSignModule {
    fun provideContractSignApi(retrofit: Retrofit): Provider<ContractSignApi> {
        return DoubleCheck.provider(
            object : Factory<ContractSignApi> {
                override fun get(): ContractSignApi {
                    return retrofit.create(ContractSignApi::class.java)
                }
            }
        )
    }

    fun provideContractSignNetworkDataSource(
        contractSignApi: ContractSignApi
    ): Provider<ContractSignNetworkDataSource> {
        return DoubleCheck.provider(
            object : Factory<ContractSignNetworkDataSource> {
                override fun get(): ContractSignNetworkDataSource {
                    return ContractSignRetrofitDataSource(contractSignApi)
                }
            }
        )
    }

    fun provideContractSignRepository(
        contractSignNetworkDataSource: ContractSignNetworkDataSource,
        identificationLocalDataSource: IdentificationLocalDataSource,
            ): Provider<ContractSignRepository> {
        return DoubleCheck.provider(
            object : Factory<ContractSignRepository> {
                override fun get(): ContractSignRepository {
                    return ContractSignRepositoryImpl(
                        contractSignNetworkDataSource,
                        identificationLocalDataSource
                    )
                }
            }
        )
    }


}