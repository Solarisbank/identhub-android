package de.solarisbank.identhub.session.data.repository

import de.solarisbank.identhub.session.data.datasource.IdentityInitializationSharedPrefsDataSource
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

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