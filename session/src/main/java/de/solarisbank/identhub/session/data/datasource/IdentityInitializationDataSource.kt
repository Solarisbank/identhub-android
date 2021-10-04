package de.solarisbank.identhub.session.data.datasource

import de.solarisbank.sdk.data.dto.InitializationDto

interface IdentityInitializationDataSource {

    fun saveInitializationDto(initializationDto: InitializationDto)
    fun getInitializationDto(): InitializationDto?
    fun deleteInitializationDto()

}