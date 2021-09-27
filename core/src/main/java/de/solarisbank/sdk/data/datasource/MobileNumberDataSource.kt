package de.solarisbank.sdk.data.datasource

import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.data.dto.MobileNumberDto
import io.reactivex.Single

class MobileNumberDataSource(private val mobileNumberApi: MobileNumberApi) {

    fun getMobileNumber(): Single<MobileNumberDto> {
        return mobileNumberApi.getMobileNumber()
    }

}