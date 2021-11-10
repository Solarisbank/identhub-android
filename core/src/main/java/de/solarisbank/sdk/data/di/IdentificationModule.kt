package de.solarisbank.sdk.data.di

import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSourceImpl
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.data.di.datasource.IdentificationInMemoryDataSourceFactory
import de.solarisbank.sdk.data.repository.IdentificationRepository
import de.solarisbank.sdk.feature.di.internal.Provider
import retrofit2.Retrofit

class IdentificationModule {

    fun provideIdentificationStatusApi(retrofit: Retrofit): IdentificationApi {
        return retrofit.create(IdentificationApi::class.java)
    }

    fun provideIdentificationRetorofitDataSource(identificationApi: IdentificationApi): IdentificationRetrofitDataSourceImpl {
        return IdentificationRetrofitDataSourceImpl(identificationApi)
    }

    fun provideIdentificationLocalDataSource(): Provider<out IdentificationLocalDataSource> {
        return IdentificationInMemoryDataSourceFactory.create()
    }


    fun provideIdentificationStatusRepository(
        identificationLocalDataSource: IdentificationLocalDataSource,
        identificationStatusRetrofitDataSource: IdentificationRetrofitDataSource,
        mobileNumberDataSource: MobileNumberDataSource
    ): IdentificationRepository {
        return IdentificationRepository(
                identificationLocalDataSource,
                identificationStatusRetrofitDataSource,
                mobileNumberDataSource
                )
    }

}