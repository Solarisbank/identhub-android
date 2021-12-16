package de.solarisbank.sdk.fourthline.domain.person

import de.solarisbank.identhub.session.data.di.IdentityInitializationSharedPrefsDataSourceFactory
import de.solarisbank.sdk.data.datasource.IdentificationInMemoryDataSource
import de.solarisbank.sdk.data.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.PersonDataDto
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.domain.model.result.succeeded
import de.solarisbank.sdk.domain.model.result.throwable
import de.solarisbank.sdk.fourthline.data.dto.FourthlineStepParametersDto
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceFactory
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceImpl
import de.solarisbank.sdk.fourthline.data.step.parameters.FourthlineStepParametersRepository
import de.solarisbank.sdk.fourthline.di.FourthlineTestComponent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class PersonDataUseCaseTest : StringSpec({

    val personDataResponse =
        "{" +
                "\"first_name\":\"FIRST_NAME\"," +
                "\"last_name\":\"LAST_NAME\"," +
                "\"address\":{" +
                    "\"street\":\"Striwitzweg\"," +
                    "\"street_number\":\"6b\"," +
                    "\"city\":\"Teltow\"," +
                    "\"country\":\"DEU\"," +
                    "\"postal_code\":\"14513\"" +
                "}," +
                "\"email\":\"person@example.com\"," +
                "\"mobile_number\":\"+15550101\"," +
                "\"nationality\":\"DEU\"," +
                "\"birth_date\":\"1972-12-15\"," +
                "\"place_of_birth\":\"LISSABON\"," +
                "\"person_uid\":\"personUid\"," +
                "\"supported_documents\":" +
                "[" +
                    "{" +
                        "\"type\":\"Residence Permit\"," +
                        "\"issuing_countries\":[" +
                            "\"AT\",\"BE\",\"DK\",\"EE\",\"FI\",\"FR\",\"GR\",\"IS\",\"IE\",\"IT\",\"LV\",\"LI\",\"LU\",\"NL\",\"NO\",\"PL\",\"PT\",\"SK\",\"SI\",\"ES\",\"SE\",\"CH\",\"GB\"" +
                        "]" +
                    "}," +
                    "{" +
                        "\"type\":\"Driving License\"," +
                        "\"issuing_countries\":[" +
                            "\"FR\",\"IT\",\"NL\",\"GB\"" +
                        "]" +
                    "}," +
                    "{\"" +
                        "type\":\"Passport\"," +
                        "\"issuing_countries\":[\"DE\"]" +
                    "}," +
                    "{" +
                        "\"type\":\"National ID Card\"," +
                        "\"issuing_countries\":[\"DE\",\"HU\",\"IT\",\"LV\"]" +
                    "}," +
                    "{" +
                        "\"type\":\"Paper ID\"," +
                        "\"issuing_countries\":[\"IT\"]" +
                    "}" +
                "]," +
                "\"gender\":\"male\"" +
        "}"

    val ipResponse = "{\"ip\":\"93.74.217.0\"}"

    val fourthlineSigningResponse =
        "{" +
            "\"id\":\"identificationId1234\"," +
            "\"reference\":\"test_reference\"," +
            "\"url\":null,\"status\":\"pending\"," +
            "\"completed_at\":null," +
            "\"method\":\"fourthline_signing\"," +
            "\"proof_of_address_type\":null," +
            "\"proof_of_address_issued_at\":null," +
            "\"iban\":null," +
            "\"terms_and_conditions_signed_at\":null," +
            "\"authorization_expires_at\":null," +
            "\"confirmation_expires_at\":null," +
            "\"provider_status_code\":null," +
            "\"estimated_waiting_time\":null" +
        "}"

    val dispatcher: Dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            println("request.path : ${request.path}")
            return when (request.path) {
                "/fourthline_signing" -> {
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(fourthlineSigningResponse)
                    }
                }
                "/identifications/identificationId1234/person_data" -> {
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(personDataResponse)
                    }
                }
                "/myip" -> {
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(ipResponse)
                    }
                }
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

    fun initDependencies(mockedIdentificationId: String, defaultToFallbackStepParam: Boolean = false): PersonDataUseCase {
        val identificationDtoMockk =  mockk<IdentificationDto>(relaxed = true) {
            every { id } returns mockedIdentificationId
            every { status = Status.UPLOAD.label } returns Unit
        }

        mockkConstructor(FourthlineStepParametersRepository::class)
        every { anyConstructed<FourthlineStepParametersRepository>().getFourthlineStepParameters() } returns
                //todo make as constants for fourthline signing and fourthline_simplified
                mockk<FourthlineStepParametersDto>() {
                    every { isFourthlineSigning } returns true
                    every { createIdentificationOnRetry } returns true
                    every { showUploadingScreen } returns false
                    every { showStepIndicator } returns false
                }

        mockkConstructor(LocationDataSourceFactory::class)
        every { anyConstructed<LocationDataSourceFactory>().get() } returns mockk<LocationDataSourceImpl>()
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

        mockkConstructor(IdentificationInMemoryDataSource::class)
        every { anyConstructed<IdentificationInMemoryDataSource>().obtainIdentificationDto() } returns Single.just(identificationDtoMockk)
        every { anyConstructed<IdentificationInMemoryDataSource>().saveIdentification(any()) } returns Completable.complete()

        return FourthlineTestComponent.getInstance(
            networkModule = NetworkModuleTestFactory(mockWebServer)
                .provideNetworkModule()
        ).personDataUseCaseProvider.get()
    }

    "fourthline_sigining_fetch_person_data" {

        val personDataUseCase: PersonDataUseCase = initDependencies("identificationId1234")
        val actualResult: Result<PersonDataDto> = personDataUseCase.execute("https://stackoverflow.com/questions/59305818/crashlytics-doesnt-display-native-crashes/").blockingGet()

        if (!actualResult.succeeded) {
            println("aChub actualResult.throwable?.message : " + actualResult.throwable?.message)
        }

        actualResult.succeeded shouldBe true
        actualResult.data!!.apply {
            firstName shouldBe "FIRST_NAME"
            lastName shouldBe "LAST_NAME"
            personUid shouldBe "personUid"
        }
    }

    //todo extend with fourthline simplified

    afterSpec {
        mockWebServer.shutdown()
    }

})