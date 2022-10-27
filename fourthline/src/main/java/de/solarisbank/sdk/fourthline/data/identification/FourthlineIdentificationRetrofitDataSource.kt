package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single

interface FourthlineIdentificationDataSource {
    fun postFourthlineIdentification(): Single<IdentificationDto>
    fun postFourthlineSigningIdentification(): Single<IdentificationDto>
}

class FourthlineIdentificationRetrofitDataSource(
        private val fourthlineIdentificationApi: FourthlineIdentificationApi
        ): FourthlineIdentificationDataSource {

    override fun postFourthlineIdentification(): Single<IdentificationDto> {
        return fourthlineIdentificationApi.postFourthlineIdentication()
    }

    override fun postFourthlineSigningIdentification(): Single<IdentificationDto> {
        return fourthlineIdentificationApi.postFourthlineSigningIdentication()
    }

}