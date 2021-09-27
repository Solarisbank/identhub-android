package de.solarisbank.sdk.data.di

import de.solarisbank.sdk.data.api.IdentificationApi
import de.solarisbank.sdk.data.dao.IdentificationDao
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSource
import de.solarisbank.sdk.data.datasource.IdentificationRetrofitDataSourceImpl
import de.solarisbank.sdk.data.datasource.IdentificationRoomDataSource
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.data.repository.IdentificationRepository
import retrofit2.Retrofit

class IdentificationModule {

    fun provideIdentificationStatusApi(retrofit: Retrofit): IdentificationApi {
        return retrofit.create(IdentificationApi::class.java)
    }

    fun provideIdentificationRetorofitDataSource(identificationApi: IdentificationApi): IdentificationRetrofitDataSourceImpl {
        return IdentificationRetrofitDataSourceImpl(identificationApi)
    }

    fun provideIdentificationRoomDataSource(identificationDao: IdentificationDao): IdentificationRoomDataSource {
        return IdentificationRoomDataSource(identificationDao)
    }

    fun provideIdentificationStatusRepository(
        identificationRoomDataSource: IdentificationRoomDataSource,
        identificationStatusRetrofitDataSource: IdentificationRetrofitDataSource,
        mobileNumberDataSource: MobileNumberDataSource
    ): IdentificationRepository {
        return IdentificationRepository(
                identificationRoomDataSource,
                identificationStatusRetrofitDataSource,
                mobileNumberDataSource
                )
    }

}