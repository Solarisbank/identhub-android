package de.solarisbank.identhub.mockro.fakes

import de.solarisbank.identhub.mockro.Mockro
import de.solarisbank.identhub.mockro.shared.MockroIdentification
import de.solarisbank.identhub.mockro.shared.PersonaConfig
import de.solarisbank.sdk.data.datasource.IdentificationRemoteDataSource
import de.solarisbank.sdk.data.datasource.MobileNumberNetworkDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.InitializationDto
import de.solarisbank.sdk.data.dto.InitializationInfoDto
import de.solarisbank.sdk.data.dto.MobileNumberDto
import de.solarisbank.sdk.data.initial.InitializationDataSource
import io.reactivex.Single

class FakeInitializationDataSource: InitializationDataSource {
    var failures: Int = 0
    override suspend fun fetchInitialization(): InitializationDto {
        val expectedFailures = Mockro.flowOptions?.numberOfStartUpFailures ?: 0
        if (failures < expectedFailures) {
            failures++
            throw Throwable("Current failures: $failures, expected: $expectedFailures")
        }
        return InitializationDto(
            firstStep = PersonaConfig.current.firstStep,
            fallbackStep = null,
            allowedRetries = 5,
            fourthlineProvider = "SolarisBank",
            partnerSettings = null
        )
    }

    override suspend fun fetchInitializationInfo(): InitializationInfoDto {
        return InitializationInfoDto(
            termsAccepted = true,
            phoneVerified = true,
            style = null,
            sdkLogging = false,
            secondaryDocScanRequired = null
        )
    }
}

class FakeIdentificationRemoteDataSource: IdentificationRemoteDataSource {
    override fun getIdentification(identificationId: String): Single<IdentificationDto> {
        return Single.just(MockroIdentification.get())
    }

    override suspend fun fetchIdentification(identificationId: String): IdentificationDto {
        return MockroIdentification.get()
    }
}

class FakeMobileNumberNetworkSource: MobileNumberNetworkDataSource {
    override fun getMobileNumber(): Single<MobileNumberDto> {
        return Single.just(MobileNumberDto("+123456789"))
    }

}