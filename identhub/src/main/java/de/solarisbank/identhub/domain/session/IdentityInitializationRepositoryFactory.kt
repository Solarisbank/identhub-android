package de.solarisbank.identhub.domain.session

import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.di.internal.Provider

class IdentityInitializationRepositoryFactory private constructor(
    private val identityInitializationSharedPrefsDataSource: Provider<IdentityInitializationSharedPrefsDataSource>
) : Factory<IdentityInitializationRepository> {
    override fun get(): IdentityInitializationRepository {
        return Preconditions.checkNotNull(
            IdentityInitializationRepositoryImpl(identityInitializationSharedPrefsDataSource.get()),
            "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            identityInitializationSharedPrefsDataSource: Provider<IdentityInitializationSharedPrefsDataSource>
        ): IdentityInitializationRepositoryFactory {
            return IdentityInitializationRepositoryFactory(
                identityInitializationSharedPrefsDataSource
            )
        }
    }
}