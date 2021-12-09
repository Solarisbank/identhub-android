package de.solarisbank.sdk.fourthline.domain.kyc.upload

import de.solarisbank.identhub.session.data.di.IdentityInitializationSharedPrefsDataSourceFactory
import de.solarisbank.identhub.session.feature.navigation.router.NEXT_STEP_DIRECTION
import de.solarisbank.sdk.data.datasource.IdentificationInMemoryDataSource
import de.solarisbank.sdk.data.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.Status
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceFactory
import de.solarisbank.sdk.fourthline.data.location.LocationDataSourceImpl
import de.solarisbank.sdk.fourthline.di.FourthlineTestComponent
import de.solarisbank.sdk.fourthline.domain.dto.KycUploadStatusDto
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempfile
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
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


class KycUploadUseCaseTest : StringSpec({

    val uploadSuccessfulResponse = ""

    val uploadFailedInvalidDataResponse =
        "{" +
            "\"id\":\"1addc4d1f836a84893c91100be254c16cidt\"," +
            "\"url\":null," +
            "\"status\":\"failed\"," +
            "\"failure_reason\":\"invalid_data\"," +
            "\"method\":\"fourthline\"," +
            "\"authorization_expires_at\":null," +
            "\"confirmation_expires_at\":null," +
            "\"provider_status_code\":\"1036\"," +
            "\"next_step\":\"fourthline/simplified\"," +
            "\"fallback_step\":null," +
            "\"documents\":[]," +
            "\"current_reference_token\":null," +
            "\"reference\":\"9fae9173-0ec7-4a14-9be2-84570283e29c\"" +
        "}"

    val pollingResponseStatusFailed =
        "{" +
                "\"id\":\"abff9109284dcb137b1742f8b6a4ee28cidt\"," +
                "\"url\":\"https://solarisbank.com/index.html?session_key=GorX6tTlliWGk0JBxfy1ro2if4YHa6uU36Vv1uKs\\u0026interface_id=31de\"," +
                "\"status\":\"failed\"," +
                "\"failure_reason\":null," +
                "\"method\":\"fourthline_signing\"," + //todo check
                "\"authorization_expires_at\":\"2021-11-28T13:22:15.000Z\"," +
                "\"confirmation_expires_at\":null," +
                "\"provider_status_code\":null," +
                "\"next_step\":\"null\"," +
                "\"fallback_step\":null," +
                "\"documents\":[" +
                "{" +
                "\"id\":\"161cac050e56108790d54cd873801fb1cdoc\"," +
                "\"name\":\"abff9109284dcb137b1742f8b6a4ee28cidt_prepared_for_signing_kyc_report.pdf\"," +
                "\"content_type\":\"application/pdf\"," +
                "\"document_type\":\"QES_DOCUMENT\"," +
                "\"size\":88473," +
                "\"customer_accessible\":false," +
                "\"created_at\":\"2021-11-28T13:11:47.000Z\"" +
                "}" +
                "]," +
                "\"current_reference_token\":null," +
                "\"reference\":null" +
                "}"

    val uploadAuthRequiredResponse =
        "{" +
            "\"id\":\"response222222\"," +
            "\"url\":null," +
            "\"status\":\"authorization_required\"," +
            "\"failure_reason\":null," +
            "\"method\":\"fourthline_signing\"," +
            "\"authorization_expires_at\":null," +
            "\"confirmation_expires_at\":null," +
            "\"provider_status_code\":null," +
            "\"next_step\":\"fourthline_signing/qes\"," +
            "\"fallback_step\":null," +
            "\"documents\":[" +
                "{" +
                "\"id\":\"adc71fb0572c57bf8b318786cfa0193acdoc\"," +
                "\"name\":\"78aae8893944b8c505bb96b3f31a0b22cidt_prepared_for_signing_terms_and_conditions.pdf\"," +
                "\"content_type\":\"application/pdf\"," +
                "\"document_type\":\"QES_DOCUMENT\"," +
                "\"size\":87049," +
                "\"customer_accessible\":false," +
                "\"created_at\":\"2021-11-25T16:09:45.000Z\"" +
                "}" +
            "]," +
            "\"current_reference_token\":null," +
            "\"reference\":\"2ace67f0-9828-4e38-bc05-d848c1095687\"" +
        "}"

    val pollingResponseToNextStepFourthlineSigningQes =
        "{" +
                "\"id\":\"abff9109284dcb137b1742f8b6a4ee28cidt\"," +
                "\"url\":\"https://solarisbank.com/index.html?session_key=GorX6tTlliWGk0JBxfy1ro2if4YHa6uU36Vv1uKs\\u0026interface_id=31de\"," +
                "\"status\":\"authorization_required\"," +
                "\"failure_reason\":null," +
                "\"method\":\"fourthline_signing\"," + //todo check
                "\"authorization_expires_at\":\"2021-11-28T13:22:15.000Z\"," +
                "\"confirmation_expires_at\":null," +
                "\"provider_status_code\":null," +
                "\"next_step\":\"fourthline_signing/qes\"," +
                "\"fallback_step\":null," +
                "\"documents\":[" +
                    "{" +
                       "\"id\":\"161cac050e56108790d54cd873801fb1cdoc\"," +
                        "\"name\":\"abff9109284dcb137b1742f8b6a4ee28cidt_prepared_for_signing_kyc_report.pdf\"," +
                        "\"content_type\":\"application/pdf\"," +
                        "\"document_type\":\"QES_DOCUMENT\"," +
                        "\"size\":88473," +
                        "\"customer_accessible\":false," +
                        "\"created_at\":\"2021-11-28T13:11:47.000Z\"" +
                    "}" +
                "]," +
                "\"current_reference_token\":null," +
                "\"reference\":null" +
            "}"

    val upload_Fourthline_Simplified_AuthRequiredResponse =
        "{" +
                "\"id\":\"response222222\"," +
                "\"url\":null," +
                "\"status\":\"authorization_required\"," +
                "\"failure_reason\":null," +
                "\"method\":\"fourthline/simplified\"," +
                "\"authorization_expires_at\":null," +
                "\"confirmation_expires_at\":null," +
                "\"provider_status_code\":null," +
                "\"next_step\":\"fourthline/simplified/qes\"," +
                "\"fallback_step\":null," +
                "\"documents\":[" +
                "{" +
                "\"id\":\"adc71fb0572c57bf8b318786cfa0193acdoc\"," +
                "\"name\":\"78aae8893944b8c505bb96b3f31a0b22cidt_prepared_for_signing_terms_and_conditions.pdf\"," +
                "\"content_type\":\"application/pdf\"," +
                "\"document_type\":\"QES_DOCUMENT\"," +
                "\"size\":87049," +
                "\"customer_accessible\":false," +
                "\"created_at\":\"2021-11-25T16:09:45.000Z\"" +
                "}" +
                "]," +
                "\"current_reference_token\":null," +
                "\"reference\":\"2ace67f0-9828-4e38-bc05-d848c1095687\"" +
                "}"

    val pollingResponseToNextStepFourthlineSimplifiedQes =
        "{" +
                "\"id\":\"abff9109284dcb137b1742f8b6a4ee28cidt\"," +
                "\"url\":\"https://solarisbank.com/index.html?session_key=GorX6tTlliWGk0JBxfy1ro2if4YHa6uU36Vv1uKs\\u0026interface_id=31de\"," +
                "\"status\":\"authorization_required\"," +
                "\"failure_reason\":null," +
                "\"method\":\"fourthline_signing\"," + //todo check
                "\"authorization_expires_at\":\"2021-11-28T13:22:15.000Z\"," +
                "\"confirmation_expires_at\":null," +
                "\"provider_status_code\":null," +
                "\"next_step\":\"fourthline_signing/qes\"," +
                "\"fallback_step\":null," +
                "\"documents\":[" +
                "{" +
                "\"id\":\"161cac050e56108790d54cd873801fb1cdoc\"," +
                "\"name\":\"abff9109284dcb137b1742f8b6a4ee28cidt_prepared_for_signing_kyc_report.pdf\"," +
                "\"content_type\":\"application/pdf\"," +
                "\"document_type\":\"QES_DOCUMENT\"," +
                "\"size\":88473," +
                "\"customer_accessible\":false," +
                "\"created_at\":\"2021-11-28T13:11:47.000Z\"" +
                "}" +
                "]," +
                "\"current_reference_token\":null," +
                "\"reference\":null" +
                "}"


    val dispatcher: Dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            println("request.path : ${request.path}")
            return when (request.path) {
                "/fourthline_identification/0001/data" -> {
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(uploadFailedInvalidDataResponse)
                    }
                }
                "/identifications/0001" -> {
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(pollingResponseStatusFailed)
                    }
                }
                "/fourthline_identification/0003/data" -> {
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(uploadAuthRequiredResponse)
                    }
                }
                "/identifications/0003" -> {
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(pollingResponseToNextStepFourthlineSigningQes)
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

    fun initDependencies(
        mockedIdentificationId: String,
        defaultToFallbackStepParam: Boolean = false
    ): KycUploadUseCase {

        val identificationDtoMockk =  mockk<IdentificationDto>(relaxed = true) {
            every { id } returns mockedIdentificationId
            every { status = Status.UPLOAD.label } returns Unit
        }

        mockkConstructor(IdentificationInMemoryDataSource::class)
        every { anyConstructed<IdentificationInMemoryDataSource>().obtainIdentificationDto() } returns
                Single.just(identificationDtoMockk)
        every { anyConstructed<IdentificationInMemoryDataSource>().saveIdentification(any()) } returns
                Completable.complete()

        mockkConstructor(LocationDataSourceFactory::class)
        every { anyConstructed<LocationDataSourceFactory>().get() } returns
                mockk<LocationDataSourceImpl>()

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


        return FourthlineTestComponent.getInstance(
            networkModule = NetworkModuleTestFactory(mockWebServer)
                .provideNetworkModule()
        ).kycUploadUseCaseProvider.get()
    }

    fun getTestFile(): File {
        val testFile = tempfile("1","txt")
        val stringContent = "{\"id\":\"request111111\"}"
        val lines: List<String> = Arrays.asList(stringContent)
        val textFile: Path = Paths.get(testFile.toURI())
        Files.write(textFile, lines, StandardCharsets.UTF_8)
        val read: List<String> = Files.readAllLines(textFile, StandardCharsets.UTF_8)
        println(lines == read)
        return testFile
    }

    "upload_failed_fourthline_signing_invalid_data" {

        val kycUploadUseCase = initDependencies("0001")
        val expectedResult = KycUploadStatusDto.GenericError

        val resultKycUploadStatusDto: KycUploadStatusDto = kycUploadUseCase
            .uploadKyc(getTestFile())
            .blockingGet()

        resultKycUploadStatusDto shouldBe expectedResult
    }

    "upload_successful_status_auth_required_ToNextStep_fourthline_signing/qes_response" {

        val kycUploadUseCase = initDependencies("0003")
        val expectedResult = KycUploadStatusDto
            .ToNextStepSuccess(NEXT_STEP_DIRECTION.FOURTHLINE_SIGNING_QES.destination)

        val resultKycUploadStatusDto: KycUploadStatusDto = kycUploadUseCase
            .uploadKyc(getTestFile())
            .blockingGet()

        resultKycUploadStatusDto shouldBe expectedResult
    }







})