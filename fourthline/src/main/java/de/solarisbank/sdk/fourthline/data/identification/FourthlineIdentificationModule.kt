package de.solarisbank.sdk.fourthline.data.identification


import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.fourthline.data.person.PersonDataDataSource
import retrofit2.Retrofit

class FourthlineIdentificationModule {

    fun provideFourthlineIdentificationApi(retrofit: Retrofit): FourthlineIdentificationApi {
        return retrofit.create(FourthlineIdentificationApi::class.java)
    }

    fun provideFourthlineIdentificationRetrofitDataSource(
        fourthlineIdentificationApi: FourthlineIdentificationApi
    ): FourthlineIdentificationRetrofitDataSource{
        return FourthlineIdentificationRetrofitDataSource(fourthlineIdentificationApi)
    }


    fun provideFourthlineIdentificationRepository(
        fourthlineIdentificationRetrofitDataSource: FourthlineIdentificationRetrofitDataSource,
        identificationLocalDataSource: IdentificationLocalDataSource,
        personDataDataSource: PersonDataDataSource
    ): FourthlineIdentificationRepository {
        return FourthlineIdentificationRepository(
                fourthlineIdentificationRetrofitDataSource,
                identificationLocalDataSource,
                personDataDataSource
        )
    }
}