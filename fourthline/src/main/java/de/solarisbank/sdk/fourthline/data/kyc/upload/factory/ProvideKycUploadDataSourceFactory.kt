package de.solarisbank.sdk.fourthline.data.kyc.upload.factory

import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadApi
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadModule
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRetrofitDataSource

class ProvideKycUploadDataSourceFactory private constructor(
        private val kycUploadModule: KycUploadModule,
        private val kycUploadApiProvider: Provider<KycUploadApi>
        ) : Factory<KycUploadRetrofitDataSource> {

    override fun get(): KycUploadRetrofitDataSource {
        return kycUploadModule.provideKycUploadDataSource(kycUploadApiProvider.get())
    }

    companion object {
        @JvmStatic
        fun create(kycUploadModule: KycUploadModule, kycUploadApiProvider: Provider<KycUploadApi>): ProvideKycUploadDataSourceFactory {
            return ProvideKycUploadDataSourceFactory(kycUploadModule, kycUploadApiProvider)
        }
    }

}