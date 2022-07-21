package de.solarisbank.identhub.session.data.repository

import de.solarisbank.identhub.session.data.datasource.IdentityInitializationDataSource
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

class IdentityInitializationRepositoryFactory private constructor(
    private val identityInitializationInMemoryDataSource: Provider<IdentityInitializationDataSource>
) : Factory<IdentityInitializationRepository> {
    override fun get(): IdentityInitializationRepository {
        return Preconditions.checkNotNull(
            IdentityInitializationRepositoryImpl(identityInitializationInMemoryDataSource.get()),
            "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            identityInitializationInMemoryDataSource: Provider<IdentityInitializationDataSource>
        ): IdentityInitializationRepositoryFactory {
            return IdentityInitializationRepositoryFactory(
                identityInitializationInMemoryDataSource
            )
        }
    }
}