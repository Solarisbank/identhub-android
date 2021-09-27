package de.solarisbank.identhub.session.data.mapper.factory

import de.solarisbank.identhub.session.data.Mapper
import de.solarisbank.identhub.session.data.mapper.MapperModule
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.IdentificationWithDocument
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions

class IdentificationEntityMapperFactory(private val mapperModule: MapperModule) :
    Factory<Mapper<IdentificationDto, IdentificationWithDocument>> {
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