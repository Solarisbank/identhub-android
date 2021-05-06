package de.solarisbank.sdk.fourthline.data.kyc.upload.factory

import de.solarisbank.identhub.data.session.SessionUrlLocalDataSource
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRetrofitDataSource
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRoomDataSource
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadModule
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRepository
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRetrofitDataSource

class ProviderKycUploadRepositoryFactory(
        private val kycUploadModule: KycUploadModule,
        private val fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>,
        private val fourthlineIdentificationRoomDataSourceProvider: Provider<FourthlineIdentificationRoomDataSource>,
        private val kycUploadRetrofitDataSourceProvider: Provider<KycUploadRetrofitDataSource>,
        private val sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
) : Factory<KycUploadRepository> {

    override fun get(): KycUploadRepository {
        return Preconditions.checkNotNull(
                kycUploadModule.provideKycUploadRepository(
                        fourthlineIdentificationRetrofitDataSourceProvider.get(),
                        fourthlineIdentificationRoomDataSourceProvider.get(),
                        kycUploadRetrofitDataSourceProvider.get(),
                        sessionUrlLocalDataSourceProvider.get()
                )
        )
    }

    companion object {
        @JvmStatic
        fun create(kycUploadModule: KycUploadModule,
                   fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>,
                   fourthlineIdentificationRoomDataSourceProvider: Provider<FourthlineIdentificationRoomDataSource>,
                   kycUploadRetrofitDataSourceProvider: Provider<KycUploadRetrofitDataSource>,
                   sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
        ): ProviderKycUploadRepositoryFactory {
            return ProviderKycUploadRepositoryFactory(
                    kycUploadModule,
                    fourthlineIdentificationRetrofitDataSourceProvider,
                    fourthlineIdentificationRoomDataSourceProvider,
                    kycUploadRetrofitDataSourceProvider,
                    sessionUrlLocalDataSourceProvider
            )
        }
    }
}