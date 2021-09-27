package de.solarisbank.sdk.data.repository

import de.solarisbank.sdk.data.dto.InitializationDto

interface IdentityInitializationRepository {
    fun saveInitializationDto(initializationDto: InitializationDto)
    fun getInitializationDto(): InitializationDto?
    fun deleteInitializationDto()
}