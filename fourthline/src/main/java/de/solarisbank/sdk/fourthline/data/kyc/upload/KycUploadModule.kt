package de.solarisbank.sdk.fourthline.data.kyc.upload

import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRetrofitDataSource
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRoomDataSource
import retrofit2.Retrofit

class KycUploadModule {

    fun provideKycUploadApi(retrofit: Retrofit): KycUploadApi {
        return retrofit.create(KycUploadApi::class.java)
    }

    fun provideKycUploadDataSource(kycUploadApi: KycUploadApi): KycUploadRetrofitDataSource {
        return KycUploadRetrofitDataSource(kycUploadApi)
    }

    fun provideKycUploadRepository(
            fourthlineIdentificationRetrofitDataSource: FourthlineIdentificationRetrofitDataSource,
            fourthlineIdentificationRoomDataSource: FourthlineIdentificationRoomDataSource,
            kycUploadRetrofitDataSource: KycUploadRetrofitDataSource,
            sessionUrlLocalDataSource: SessionUrlLocalDataSource

    ): KycUploadRepository {
        return KycUploadRepository(
                fourthlineIdentificationRetrofitDataSource,
                fourthlineIdentificationRoomDataSource,
                kycUploadRetrofitDataSource,
                sessionUrlLocalDataSource
        )
    }
}