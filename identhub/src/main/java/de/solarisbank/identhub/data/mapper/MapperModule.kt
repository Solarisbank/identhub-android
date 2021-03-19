package de.solarisbank.identhub.data.mapper

import de.solarisbank.identhub.data.Mapper
import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.entity.IdentificationWithDocument

class MapperModule {
    fun provideIdentificationEntityMapper(): Mapper<IdentificationDto, IdentificationWithDocument> {
        return IdentificationEntityMapper()
    }
}