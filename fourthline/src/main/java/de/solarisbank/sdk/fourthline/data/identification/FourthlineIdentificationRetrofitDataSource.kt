package de.solarisbank.sdk.fourthline.data.identification

import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single

class FourthlineIdentificationRetrofitDataSource(
        private val fourthlineIdentificationApi: FourthlineIdentificationApi
        ) {

    fun postFourthlineIdentication(): Single<IdentificationDto> {
        return fourthlineIdentificationApi.postFourthlineIdentication()
    }

}