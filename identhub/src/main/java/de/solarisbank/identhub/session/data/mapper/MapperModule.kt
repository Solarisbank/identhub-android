package de.solarisbank.identhub.session.data.mapper

import de.solarisbank.identhub.session.data.Mapper
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.IdentificationWithDocument

class MapperModule {
    fun provideIdentificationEntityMapper(): Mapper<IdentificationDto, IdentificationWithDocument> {
        return IdentificationEntityMapper()
    }
}