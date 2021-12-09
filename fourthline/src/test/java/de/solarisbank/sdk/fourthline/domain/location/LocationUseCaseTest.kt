package de.solarisbank.sdk.fourthline.domain.location

import android.location.Location
import de.solarisbank.identhub.session.data.di.IdentityInitializationSharedPrefsDataSourceFactory
import de.solarisbank.sdk.data.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.fourthline.data.dto.LocationDto
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceFactory
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceImpl
import de.solarisbank.sdk.fourthline.di.FourthlineTestComponent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.reactivex.Single
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class LocationUseCaseTest : StringSpec({

    val dispatcher: Dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            println("request.path : ${request.path}")
            return when (request.path) {

                else -> {
                    MockResponse().apply {
                        setResponseCode(400)
                    }
                }
            }
        }
    }

    val mockWebServer = MockWebServer().apply {
        this.dispatcher = dispatcher
    }

    val locationMockk = mockk<Location>() {
        every { longitude } returns 1111.0
        every { latitude } returns 1111.0
    }

    fun initLocationUseCase(
        locationDto: LocationDto,
        defaultToFallbackStepParam: Boolean = false
    ): LocationUseCase {

        mockkConstructor(IdentityInitializationSharedPrefsDataSourceFactory::class)
        every { anyConstructed<IdentityInitializationSharedPrefsDataSourceFactory>().get() } returns
                mockk {
                    every { getInitializationDto() } returns
                            mockk {
                                every { partnerSettings } returns
                                        mockk {
                                            every { defaultToFallbackStep } returns
                                                    defaultToFallbackStepParam
                                        }
                            }
                }

        val locationDataSourceImplMockk = mockk<LocationDataSourceImpl>() {
            every { getLocation() } returns Single.just(locationDto)
        }

        mockkConstructor(LocationDataSourceFactory::class)
        every { anyConstructed<LocationDataSourceFactory>().get() } returns
                locationDataSourceImplMockk

        return FourthlineTestComponent.getInstance(
            networkModule = NetworkModuleTestFactory(mockWebServer)
                .provideNetworkModule()
        ).locationUseCaseProvider.get()
    }


    "verifyLocationSuccess" {
        val expectedLocationDto = LocationDto.SUCCESS(locationMockk)
        val locationUseCase: LocationUseCase = initLocationUseCase(expectedLocationDto)
        val actualLocationDto: LocationDto = locationUseCase.getLocation().blockingGet()
        actualLocationDto shouldBe expectedLocationDto
    }

    "verifyLocationNetworkNotEnabledError" {
        val expectedLocationDto = LocationDto.NETWORK_NOT_ENABLED_ERROR
        val locationUseCase: LocationUseCase = initLocationUseCase(expectedLocationDto)
        val actualLocationDto: LocationDto = locationUseCase.getLocation().blockingGet()
        actualLocationDto shouldBe expectedLocationDto
    }

    "verifyLocationClientNotEnabledError" {
        val expectedLocationDto = LocationDto.LOCATION_CLIEN_NOT_ENABLED_ERROR
        val locationUseCase: LocationUseCase = initLocationUseCase(expectedLocationDto)
        val actualLocationDto: LocationDto = locationUseCase.getLocation().blockingGet()
        actualLocationDto shouldBe expectedLocationDto
    }

    "verifyLocationFetchingError" {
        val expectedLocationDto = LocationDto.LOCATION_FETCHING_ERROR
        val locationUseCase: LocationUseCase = initLocationUseCase(expectedLocationDto)
        val actualLocationDto: LocationDto = locationUseCase.getLocation().blockingGet()
        actualLocationDto shouldBe expectedLocationDto
    }

})