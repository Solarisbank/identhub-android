package de.solarisbank.identhub.domain.session

import de.solarisbank.identhub.domain.data.dto.InitializationDto

interface IdentityInitializationRepository {
    fun saveInitializationDto(initializationDto: InitializationDto)
    fun getInitializationDto(): InitializationDto?
    fun deleteInitializationDto()
}