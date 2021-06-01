package de.solarisbank.identhub.session.data.identification

import de.solarisbank.identhub.data.dao.IdentificationDao
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
            identificationStatusRetrofitDataSource: IdentificationRetrofitDataSourceImpl
    ): IdentificationRepository {
        return IdentificationRepository(identificationRoomDataSource, identificationStatusRetrofitDataSource)
    }

}