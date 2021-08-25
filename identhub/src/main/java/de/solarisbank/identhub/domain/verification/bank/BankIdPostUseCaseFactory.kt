package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.domain.session.IdentityInitializationRepository
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Provider

class BankIdPostUseCaseFactory private constructor(
    private val verificationBankRepositoryProvider: Provider<VerificationBankRepository>,
    private val identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
) : Factory<BankIdPostUseCase> {

    override fun get(): BankIdPostUseCase {
        return BankIdPostUseCase(
            verificationBankRepositoryProvider.get(),
            identityInitializationRepositoryProvider.get()
        )
    }

    companion object {
        fun create(
            verificationBankRepositoryProvider: Provider<VerificationBankRepository>,
            identityInitializationRepositoryProvider: Provider<IdentityInitializationRepository>
        ): BankIdPostUseCaseFactory {
            return BankIdPostUseCaseFactory(
                verificationBankRepositoryProvider,
                identityInitializationRepositoryProvider
            )
        }
    }
}