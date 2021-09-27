package de.solarisbank.sdk.fourthline.domain.kyc.storage

import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoRepository

class KycInfoUseCaseFactory private constructor(
    private val kycInfoRepositoryProvider: Provider<KycInfoRepository>,
    private val identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
) : Factory<KycInfoUseCase> {
    override fun get(): KycInfoUseCase {
        return Preconditions.checkNotNull(
                KycInfoUseCase(
                        kycInfoRepositoryProvider.get(),
                        identityInitializationRepositoryProvider.get()
                ),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>,
            kycInfoRepositoryProvider: Provider<KycInfoRepository>
        ): KycInfoUseCaseFactory {
            return KycInfoUseCaseFactory(
                    kycInfoRepositoryProvider,
                    identityInitializationRepositoryProvider
            )
        }
    }
}
