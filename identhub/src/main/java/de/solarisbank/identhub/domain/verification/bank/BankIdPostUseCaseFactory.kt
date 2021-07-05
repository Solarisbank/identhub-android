package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Provider

class BankIdPostUseCaseFactory private constructor(
        private val verificationBankRepositoryProvider: Provider<VerificationBankRepository>
        ): Factory<JointAccountBankIdPostUseCase> {

    override fun get(): JointAccountBankIdPostUseCase {
        return JointAccountBankIdPostUseCase(verificationBankRepositoryProvider.get())
    }

    companion object {
        fun create(verificationBankRepositoryProvider: Provider<VerificationBankRepository>): BankIdPostUseCaseFactory {
            return BankIdPostUseCaseFactory(verificationBankRepositoryProvider)
        }
    }
}