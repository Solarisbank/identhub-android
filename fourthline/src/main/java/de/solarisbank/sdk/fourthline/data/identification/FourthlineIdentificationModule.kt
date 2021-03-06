package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.identhub.data.dao.IdentificationDao
import de.solarisbank.identhub.session.data.identification.IdentificationRoomDataSource
import retrofit2.Retrofit

class FourthlineIdentificationModule {

    fun provideFourthlineIdentificationApi(retrofit: Retrofit): FourthlineIdentificationApi {
        return retrofit.create(FourthlineIdentificationApi::class.java)
    }

    fun provideFourthlineIdentificationRetrofitDataSource(fourthlineIdentificationApi: FourthlineIdentificationApi): FourthlineIdentificationRetrofitDataSource{
        return FourthlineIdentificationRetrofitDataSource(fourthlineIdentificationApi)
    }

    fun provideFourthlineIdentificationRoomDataSource(identificationDao: IdentificationDao): IdentificationRoomDataSource {
        return IdentificationRoomDataSource(identificationDao)
    }

    fun provideFourthlineIdentificationRepository(
            fourthlineIdentificationRetrofitDataSource: FourthlineIdentificationRetrofitDataSource,
            identificationRoomDataSource: IdentificationRoomDataSource
    ): FourthlineIdentificationRepository {
        return FourthlineIdentificationRepository(
                fourthlineIdentificationRetrofitDataSource,
                identificationRoomDataSource
        )
    }
}