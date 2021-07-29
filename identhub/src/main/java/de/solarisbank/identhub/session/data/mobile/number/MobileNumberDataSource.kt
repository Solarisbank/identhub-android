package de.solarisbank.identhub.session.data.mobile.number

import de.solarisbank.identhub.domain.data.dto.MobileNumberDto
import io.reactivex.Single

class MobileNumberDataSource(val mobileNumberApi: MobileNumberApi) {

    fun getMobileNumber(): Single<MobileNumberDto> {
        return mobileNumberApi.getMobileNumber()
    }

}