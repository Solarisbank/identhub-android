package de.solarisbank.sdk.fourthline.data.kyc.upload.factory

import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadApi
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadModule
import retrofit2.Retrofit

class ProviderKycUploadApiFactory private constructor(
        private val kycUploadModule: KycUploadModule,
        private val retrofitProvider: Provider<Retrofit>
        ) : Factory<KycUploadApi> {

    override fun get(): KycUploadApi {
        return Preconditions.checkNotNull(
                kycUploadModule.provideKycUploadApi(retrofitProvider.get())
        )
    }

    companion object {
        @JvmStatic
        fun create(
                kycUploadModule: KycUploadModule,
                retrofitProvider: Provider<Retrofit>
        ): ProviderKycUploadApiFactory {
            return ProviderKycUploadApiFactory(kycUploadModule, retrofitProvider)
        }
    }
}