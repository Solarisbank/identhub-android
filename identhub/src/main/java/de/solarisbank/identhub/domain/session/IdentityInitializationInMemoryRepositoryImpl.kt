package de.solarisbank.identhub.domain.session

import de.solarisbank.identhub.domain.data.dto.InitializationDto

class IdentityInitializationRepositoryImpl(private val identityInitializationSharedPrefsDataSource: IdentityInitializationSharedPrefsDataSource) :
    IdentityInitializationRepository {

    override fun saveInitializationDto(initializationDto: InitializationDto) {
        identityInitializationSharedPrefsDataSource.saveInitializationDto(initializationDto)
    }

    override fun getInitializationDto(): InitializationDto? {
        return identityInitializationSharedPrefsDataSource.getInitializationDto()
    }

    override fun deleteInitializationDto() {
        identityInitializationSharedPrefsDataSource.deleteInitializationDto()
    }
}