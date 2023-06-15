package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.sdk.data.dto.IdentificationDto

interface FourthlineIdentificationDataSource {
    suspend fun createFourthlineIdentification(): IdentificationDto
    suspend fun createFourthlineSigningIdentification(): IdentificationDto
}

class FourthlineIdentificationRetrofitDataSource(
    private val api: FourthlineIdentificationApi,
) : FourthlineIdentificationDataSource {
    override suspend fun createFourthlineIdentification(): IdentificationDto {
        return api.createFourthlineIdentification()
    }

    override suspend fun createFourthlineSigningIdentification(): IdentificationDto {
        return api.createFourthlineSigningIdentification()
    }

}