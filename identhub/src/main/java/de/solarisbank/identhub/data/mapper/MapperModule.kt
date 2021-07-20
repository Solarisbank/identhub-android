package de.solarisbank.identhub.data.mapper

import de.solarisbank.identhub.data.Mapper
import de.solarisbank.identhub.data.entity.IdentificationWithDocument
import de.solarisbank.identhub.domain.data.dto.IdentificationDto

class MapperModule {
    fun provideIdentificationEntityMapper(): Mapper<IdentificationDto, IdentificationWithDocument> {
        return IdentificationEntityMapper()
    }
}