package de.solarisbank.sdk.data.datasource

import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.data.dto.MobileNumberDto

interface MobileNumberDataSource {
    suspend fun getMobileNumber(): MobileNumberDto
}

class MobileNumberDataSourceImpl(private val mobileNumberApi: MobileNumberApi) :
    MobileNumberDataSource {

    override suspend fun getMobileNumber(): MobileNumberDto {
        return mobileNumberApi.getMobileNumber()
    }

}