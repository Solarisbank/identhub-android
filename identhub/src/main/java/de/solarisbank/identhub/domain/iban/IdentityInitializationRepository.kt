package de.solarisbank.identhub.domain.iban

import de.solarisbank.identhub.domain.data.dto.InitializationDto

interface IdentityInitializationRepository {
    fun saveInitializationDto(initializationDto: InitializationDto)
    fun getInitializationDto(): InitializationDto?
    fun deleteInitializationDto()
}