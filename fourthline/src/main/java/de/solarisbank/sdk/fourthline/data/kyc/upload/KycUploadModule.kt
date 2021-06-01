package de.solarisbank.sdk.fourthline.data.kyc.upload

import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource
import de.solarisbank.identhub.session.data.identification.IdentificationRoomDataSource
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRetrofitDataSource
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
            identificationRoomDataSource: IdentificationRoomDataSource,
            kycUploadRetrofitDataSource: KycUploadRetrofitDataSource,
            sessionUrlLocalDataSource: SessionUrlLocalDataSource

    ): KycUploadRepository {
        return KycUploadRepository(
                identificationRoomDataSource,
                kycUploadRetrofitDataSource
        )
    }
}