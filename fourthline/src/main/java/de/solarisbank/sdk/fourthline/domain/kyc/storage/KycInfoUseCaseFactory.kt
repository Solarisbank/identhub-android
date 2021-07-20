package de.solarisbank.sdk.fourthline.domain.kyc.storage

import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider

class KycInfoUseCaseFactory private constructor(
    private val identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
) : Factory<KycInfoUseCase> {
    override fun get(): KycInfoUseCase {
        return Preconditions.checkNotNull(
            KycInfoUseCase(
                identityInitializationRepositoryProvider.get()
            ),
            "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
        ): KycInfoUseCaseFactory {
            return KycInfoUseCaseFactory(
                identityInitializationRepositoryProvider
            )
        }
    }
}
