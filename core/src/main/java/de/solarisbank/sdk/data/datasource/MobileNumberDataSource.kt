package de.solarisbank.sdk.data.datasource

import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.data.dto.MobileNumberDto
import io.reactivex.Single

interface MobileNumberNetworkDataSource {
    fun getMobileNumber(): Single<MobileNumberDto>
}

class MobileNumberDataSource(private val mobileNumberApi: MobileNumberApi) :
    MobileNumberNetworkDataSource {

    override fun getMobileNumber(): Single<MobileNumberDto> {
        return mobileNumberApi.getMobileNumber()
    }

}