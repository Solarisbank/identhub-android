package de.solarisbank.identhub.data.mapper.factory

import de.solarisbank.identhub.data.Mapper
import de.solarisbank.identhub.data.dto.IdentificationDto
import de.solarisbank.identhub.data.entity.IdentificationWithDocument
import de.solarisbank.identhub.data.mapper.MapperModule
import de.solarisbank.identhub.di.internal.Factory
import de.solarisbank.identhub.di.internal.Preconditions

class IdentificationEntityMapperFactory(private val mapperModule: MapperModule) : Factory<Mapper<IdentificationDto, IdentificationWithDocument>> {
    override fun get(): Mapper<IdentificationDto, IdentificationWithDocument> {
        return Preconditions.checkNotNull(
                mapperModule.provideIdentificationEntityMapper(),
                "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(mapperModule: MapperModule): IdentificationEntityMapperFactory {
            return IdentificationEntityMapperFactory(mapperModule)
        }
    }
}