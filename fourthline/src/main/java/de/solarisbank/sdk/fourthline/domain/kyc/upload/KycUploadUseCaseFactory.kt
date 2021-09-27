package de.solarisbank.sdk.fourthline.domain.kyc.upload

import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.domain.usecase.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRepository
import de.solarisbank.sdk.fourthline.domain.kyc.delete.DeleteKycInfoUseCase

class KycUploadUseCaseFactory private constructor(
    private val kycUploadRepositoryProvider: Provider<KycUploadRepository>,
    private val deleteKycInfoUseCaseProvider: Provider<DeleteKycInfoUseCase>,
    private val identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>,
    private val identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>

) : Factory<KycUploadUseCase> {
    override fun get(): KycUploadUseCase {
        return Preconditions.checkNotNull(
                KycUploadUseCase(
                    kycUploadRepositoryProvider.get(),
                    deleteKycInfoUseCaseProvider.get(),
                    identificationPollingStatusUseCaseProvider.get(),
                    identityInitializationRepositoryProvider.get()
                ),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            kycUploadRepositoryProvider: Provider<KycUploadRepository>,
            deleteKycInfoUseCaseProvider: Provider<DeleteKycInfoUseCase>,
            identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>,
            identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
        ): KycUploadUseCaseFactory {
            return KycUploadUseCaseFactory(
                kycUploadRepositoryProvider,
                deleteKycInfoUseCaseProvider,
                identificationPollingStatusUseCaseProvider,
                identityInitializationRepositoryProvider
            )
        }
    }

}