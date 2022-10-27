package de.solarisbank.identhub.mockro.fakes

import de.solarisbank.identhub.mockro.shared.MockroIdentification
import de.solarisbank.identhub.mockro.shared.PersonaConfig
import de.solarisbank.sdk.data.datasource.IdentificationRemoteDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.InitializationDto
import de.solarisbank.sdk.data.dto.InitializationInfoDto
import de.solarisbank.sdk.data.initial.InitializationDataSource
import io.reactivex.Single

class FakeInitializationDataSource: InitializationDataSource {
    override fun fetchInitialization(): Single<InitializationDto> {
        return Single.just(
            InitializationDto(
                firstStep = PersonaConfig.current.firstStep,
                fallbackStep = null,
                allowedRetries = 5,
                fourthlineProvider = "SolarisBank",
                partnerSettings = null
            )
        )
    }

    override fun fetchInitializationInfo(): Single<InitializationInfoDto> {
        return Single.just(
            InitializationInfoDto(
                termsAccepted = true,
                phoneVerified = true,
                style = null,
                sdkLogging = false
            )
        )
    }
}

class FakeIdentificationRemoteDataSource: IdentificationRemoteDataSource {
    override fun getIdentification(identificationId: String): Single<IdentificationDto> {
        return Single.just(MockroIdentification.get())
    }
}