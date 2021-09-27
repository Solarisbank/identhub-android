package de.solarisbank.identhub.session.data.repository

import de.solarisbank.identhub.session.data.datasource.IdentityInitializationSharedPrefsDataSource
import de.solarisbank.sdk.data.dto.InitializationDto
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository

class IdentityInitializationRepositoryImpl(
    private val identityInitializationSharedPrefsDataSource: IdentityInitializationSharedPrefsDataSource
    ) : IdentityInitializationRepository {

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