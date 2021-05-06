package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.identhub.data.dao.IdentificationDao
import retrofit2.Retrofit

class FourthlineIdentificationModule {

    fun provideFourthlineIdentificationApi(retrofit: Retrofit): FourthlineIdentificationApi {
        return retrofit.create(FourthlineIdentificationApi::class.java)
    }

    fun provideFourthlineIdentificationRetrofitDataSource(fourthlineIdentificationApi: FourthlineIdentificationApi): FourthlineIdentificationRetrofitDataSource{
        return FourthlineIdentificationRetrofitDataSource(fourthlineIdentificationApi)
    }

    fun provideFourthlineIdentificationRoomDataSource(identificationDao: IdentificationDao): FourthlineIdentificationRoomDataSource {
        return FourthlineIdentificationRoomDataSource(identificationDao)
    }

    fun provideFourthlineIdentificationRepository(
            fourthlineIdentificationRetrofitDataSource: FourthlineIdentificationRetrofitDataSource,
            fourthlineIdentificationRoomDataSource: FourthlineIdentificationRoomDataSource
    ): FourthlineIdentificationRepository {
        return FourthlineIdentificationRepository(
                fourthlineIdentificationRetrofitDataSource,
                fourthlineIdentificationRoomDataSource
        )
    }
}