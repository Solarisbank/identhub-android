package de.solarisbank.sdk.fourthline.data.person

import de.solarisbank.sdk.feature.di.internal.Factory

class PersonDataDataSourceFactory private constructor(
        private val personDataApi: PersonDataApi
        ) :
    Factory<PersonDataDataSource> {

    override fun get(): PersonDataDataSource {
        return PersonDataDataSource(personDataApi)
    }

    companion object {
        @JvmStatic
        fun create(personDataApi: PersonDataApi) : PersonDataDataSourceFactory {
            return PersonDataDataSourceFactory(personDataApi)
        }
    }
}