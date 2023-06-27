package de.solarisbank.sdk.domain.usecase


import de.solarisbank.sdk.data.datasource.MobileNumberDataSource

interface MobileNumberUseCase {
    suspend fun fetchMobileNumber(): String
    fun maskPhoneNumber(number: String?): String
}

class MobileNumberUseCaseImpl (
    private val mobileNumberDataSource: MobileNumberDataSource
    ): MobileNumberUseCase {

    override suspend fun fetchMobileNumber(): String {
        return mobileNumberDataSource.getMobileNumber().number
    }

    override fun maskPhoneNumber(number: String?): String {
        if (number == null || number.length < 5) {
            return "****"
        }

        val maxRange = if (number.length < 9) {
            number.length / 2 + 1
        } else {
            number.length - 4
        }
        return number.replaceRange(1, maxRange, "*".repeat(maxRange - 1))
    }
}