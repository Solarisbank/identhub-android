package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Provider

class JointAccountBankIdPostUseCaseFactory private constructor(
    private val verificationBankRepositoryProvider: Provider<VerificationBankRepository>,
    private val identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
) : Factory<JointAccountBankIdPostUseCase> {

    override fun get(): JointAccountBankIdPostUseCase {
        return JointAccountBankIdPostUseCase(
            verificationBankRepositoryProvider.get(),
            identityInitializationRepositoryProvider.get()
        )
    }

    companion object {
        fun create(
            verificationBankRepositoryProvider: Provider<VerificationBankRepository>,
            identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
        ): JointAccountBankIdPostUseCaseFactory {
            return JointAccountBankIdPostUseCaseFactory(
                verificationBankRepositoryProvider,
                identityInitializationRepositoryProvider
            )
        }
    }
}