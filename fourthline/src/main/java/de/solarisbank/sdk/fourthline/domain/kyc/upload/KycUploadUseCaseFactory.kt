package de.solarisbank.sdk.fourthline.domain.kyc.upload

import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.identhub.domain.session.SessionUrlRepository
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.data.kyc.upload.KycUploadRepository

class KycUploadUseCaseFactory private constructor(
        private val kycUploadRepositoryProvider: Provider<KycUploadRepository>,
        private val sessionUrlRepositoryProvider: Provider<SessionUrlRepository>,
        private val identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>,
        private val identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>

) : Factory<KycUploadUseCase> {
    override fun get(): KycUploadUseCase {
        return Preconditions.checkNotNull(
                KycUploadUseCase(
                    kycUploadRepositoryProvider.get(),
                    sessionUrlRepositoryProvider.get(),
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
                SessionUrlRepositoryProvider: Provider<SessionUrlRepository>,
                identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>,
                identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
        ): KycUploadUseCaseFactory {
            return KycUploadUseCaseFactory(
                kycUploadRepositoryProvider,
                SessionUrlRepositoryProvider,
                identificationPollingStatusUseCaseProvider,
                identityInitializationRepositoryProvider
            )
        }
    }

}