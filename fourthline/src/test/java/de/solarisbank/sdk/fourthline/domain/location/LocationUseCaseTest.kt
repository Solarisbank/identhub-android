package de.solarisbank.sdk.fourthline.domain.location

import de.solarisbank.sdk.fourthline.data.dto.Location
import de.solarisbank.sdk.fourthline.data.dto.LocationResult
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceImpl
import de.solarisbank.sdk.fourthline.data.location.LocationRepositoryImpl
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single

class LocationUseCaseTest : StringSpec({

    val location = Location(1111.0, 1111.0)

    fun initLocationUseCase(
        locationDto: LocationResult
    ): LocationUseCase {

        val locationDataSourceImplMockk = mockk<LocationDataSourceImpl> {
            every { getLocation() } returns Single.just(locationDto)
        }

        return LocationUseCase(LocationRepositoryImpl(locationDataSourceImplMockk))
    }


    "verifyLocationSuccess" {
        val expectedLocationDto = LocationResult.Success(location)
        val locationUseCase: LocationUseCase = initLocationUseCase(expectedLocationDto)
        val actualLocationDto: LocationResult = locationUseCase.getLocation().blockingGet()
        actualLocationDto shouldBe expectedLocationDto
    }

    "verifyLocationNetworkNotEnabledError" {
        val expectedLocationDto = LocationResult.NetworkNotEnabledError
        val locationUseCase: LocationUseCase = initLocationUseCase(expectedLocationDto)
        val actualLocationDto: LocationResult = locationUseCase.getLocation().blockingGet()
        actualLocationDto shouldBe expectedLocationDto
    }

    "verifyLocationClientNotEnabledError" {
        val expectedLocationDto = LocationResult.LocationClientNotEnabledError
        val locationUseCase: LocationUseCase = initLocationUseCase(expectedLocationDto)
        val actualLocationDto: LocationResult = locationUseCase.getLocation().blockingGet()
        actualLocationDto shouldBe expectedLocationDto
    }

    "verifyLocationFetchingError" {
        val expectedLocationDto = LocationResult.LocationFetchingError
        val locationUseCase: LocationUseCase = initLocationUseCase(expectedLocationDto)
        val actualLocationDto: LocationResult = locationUseCase.getLocation().blockingGet()
        actualLocationDto shouldBe expectedLocationDto
    }

})