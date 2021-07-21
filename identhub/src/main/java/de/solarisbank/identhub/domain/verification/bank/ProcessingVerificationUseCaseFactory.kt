package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.identhub.session.domain.IdentificationPollingStatusUseCase
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Provider

class ProcessingVerificationUseCaseFactory private constructor(
    private val identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>,
    private val jointAccountBankIdPostUseCaseProvider: Provider<JointAccountBankIdPostUseCase>,
    private val identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
) : Factory<ProcessingVerificationUseCase> {

    override fun get(): ProcessingVerificationUseCase {
        return ProcessingVerificationUseCase(
            identificationPollingStatusUseCaseProvider.get(),
            jointAccountBankIdPostUseCaseProvider.get(),
            identityInitializationRepositoryProvider.get()
        )
    }

    companion object {
        fun create(
            identificationPollingStatusUseCaseProvider: Provider<IdentificationPollingStatusUseCase>,
            jointAccountBankIdPostUseCaseProvider: Provider<JointAccountBankIdPostUseCase>,
            identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
        ): ProcessingVerificationUseCaseFactory {
            return ProcessingVerificationUseCaseFactory(
                identificationPollingStatusUseCaseProvider,
                jointAccountBankIdPostUseCaseProvider,
                identityInitializationRepositoryProvider
            )
        }
    }
}