package de.solarisbank.identhub.data.mapper

import de.solarisbank.identhub.data.Mapper
import de.solarisbank.identhub.data.entity.Document
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.data.entity.IdentificationWithDocument
import de.solarisbank.identhub.domain.data.dto.DocumentDto
import de.solarisbank.identhub.domain.data.dto.IdentificationDto

class IdentificationEntityMapper : Mapper<IdentificationDto, IdentificationWithDocument> {
    override fun to(input: IdentificationDto): IdentificationWithDocument {
        return input.run {
            val documents = documents?.map { Document(it.id, it.name, it.contentType, it.documentType, it.size, it.isCustomerAccessible, it.createdAt, it.deletedAt, this.id) }
            IdentificationWithDocument(Identification(id = id, url = url?:"", method = method, status = status, nextStep = nextStep), documents
                    ?: listOf())
        }
    }

    override fun from(output: IdentificationWithDocument): IdentificationDto {
        return output.run {
            val documentsDto = documents.map { DocumentDto(it.id, it.name, it.contentType, it.documentType, it.size, it.isCustomerAccessible, it.createdAt, it.deletedAt) }
            identification.run {
                IdentificationDto(id = id, url = url, status = status, documents = documentsDto)
            }
        }
    }
}