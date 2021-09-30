package de.solarisbank.sdk.fourthline.data.kyc.upload

import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
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
        identificationRoomDataSource: IdentificationLocalDataSource,
        kycUploadRetrofitDataSource: KycUploadRetrofitDataSource,
        sessionUrlLocalDataSource: SessionUrlLocalDataSource

    ): KycUploadRepository {
        return KycUploadRepository(
                identificationRoomDataSource,
                kycUploadRetrofitDataSource
        )
    }
}