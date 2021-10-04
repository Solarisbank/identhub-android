package de.solarisbank.identhub.session.data.repository

import de.solarisbank.identhub.session.data.datasource.IdentityInitializationDataSource
import de.solarisbank.sdk.data.dto.InitializationDto
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository

class IdentityInitializationRepositoryImpl(
    private val identityInitializationDataSource: IdentityInitializationDataSource
    ) : IdentityInitializationRepository {

    override fun saveInitializationDto(initializationDto: InitializationDto) {
        identityInitializationDataSource.saveInitializationDto(initializationDto)
    }

    override fun getInitializationDto(): InitializationDto? {
        return identityInitializationDataSource.getInitializationDto()
    }

    override fun deleteInitializationDto() {
        identityInitializationDataSource.deleteInitializationDto()
    }
}