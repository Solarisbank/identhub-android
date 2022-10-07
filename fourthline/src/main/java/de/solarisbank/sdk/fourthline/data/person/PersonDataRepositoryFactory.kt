package de.solarisbank.sdk.fourthline.data.person

import de.solarisbank.sdk.feature.di.internal.Factory

class PersonDataRepositoryFactory private constructor(
        private val personDataDataSource: PersonDataDataSource
        ) :
    Factory<PersonDataRepository> {

    override fun get(): PersonDataRepository {
        return PersonDataRepository(personDataDataSource)
    }

    companion object {
        @JvmStatic
        fun create(personDataDataSource: PersonDataDataSource) : PersonDataRepositoryFactory {
            return PersonDataRepositoryFactory(personDataDataSource)
        }
    }
}