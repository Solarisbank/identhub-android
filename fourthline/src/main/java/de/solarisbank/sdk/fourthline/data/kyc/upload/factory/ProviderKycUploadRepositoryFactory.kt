package de.solarisbank.sdk.fourthline.data.kyc.upload.factory

import de.solarisbank.sdk.data.datasource.IdentificationRoomDataSource
import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.identification.FourthlineIdentificationRetrofitDataSource
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadModule
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRepository
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRetrofitDataSource

class ProviderKycUploadRepositoryFactory(
    private val kycUploadModule: KycUploadModule,
    private val fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>,
    private val identificationRoomDataSourceProvider: Provider<IdentificationRoomDataSource>,
    private val kycUploadRetrofitDataSourceProvider: Provider<KycUploadRetrofitDataSource>,
    private val sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
) : Factory<KycUploadRepository> {

    override fun get(): KycUploadRepository {
        return Preconditions.checkNotNull(
                kycUploadModule.provideKycUploadRepository(
                        fourthlineIdentificationRetrofitDataSourceProvider.get(),
                        identificationRoomDataSourceProvider.get(),
                        kycUploadRetrofitDataSourceProvider.get(),
                        sessionUrlLocalDataSourceProvider.get()
                )
        )
    }

    companion object {
        @JvmStatic
        fun create(kycUploadModule: KycUploadModule,
                   fourthlineIdentificationRetrofitDataSourceProvider: Provider<FourthlineIdentificationRetrofitDataSource>,
                   identificationRoomDataSourceProvider: Provider<IdentificationRoomDataSource>,
                   kycUploadRetrofitDataSourceProvider: Provider<KycUploadRetrofitDataSource>,
                   sessionUrlLocalDataSourceProvider: Provider<SessionUrlLocalDataSource>
        ): ProviderKycUploadRepositoryFactory {
            return ProviderKycUploadRepositoryFactory(
                    kycUploadModule,
                    fourthlineIdentificationRetrofitDataSourceProvider,
                    identificationRoomDataSourceProvider,
                    kycUploadRetrofitDataSourceProvider,
                    sessionUrlLocalDataSourceProvider
            )
        }
    }
}