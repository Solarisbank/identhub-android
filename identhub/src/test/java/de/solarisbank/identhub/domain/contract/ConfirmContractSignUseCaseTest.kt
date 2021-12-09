package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepository
import de.solarisbank.identhub.data.dto.QesStepParametersDto
import de.solarisbank.identhub.di.IdentHubTestComponent
import de.solarisbank.identhub.domain.data.dto.ContractSigningState
import de.solarisbank.sdk.data.datasource.IdentificationInMemoryDataSource
import de.solarisbank.sdk.data.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.data
import de.solarisbank.sdk.domain.model.result.succeeded
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

class ConfirmContractSignUseCaseTest : StringSpec({

    val  confirmed200_method_fourthline_signing_response =
        "{" +
                "\"id\":\"78aae8893944b8c505bb96b3f31a0b22cidt\"," +
                "\"reference\":\"2ace67f0-9828-4e38-bc05-d848c1095687\"," +
                "\"url\":null," +
                "\"status\":\"confirmed\"," +
                "\"completed_at\":null," +
                "\"method\":\"fourthline_signing\"," +
                "\"proof_of_address_type\":null," +
                "\"proof_of_address_issued_at\":null," +
                "\"iban\":null," +
                "\"terms_and_conditions_signed_at\":null," +
                "\"authorization_expires_at\":null," +
                "\"confirmation_expires_at\":null," +
                "\"provider_status_code\":null" +
                "}"

    val polling_status_confirmed_response =
        "{" +
                "\"id\":\"78aae8893944b8c505bb96b3f31a0b22cidt\"," +
                "\"url\":null," +
                "\"status\":\"confirmed\"," +
                "\"failure_reason\":null," +
                "\"method\":\"fourthline_signing\"," +
                "\"authorization_expires_at\":null," +
                "\"confirmation_expires_at\":null," +
                "\"provider_status_code\":null," +
                "\"next_step\":null," +
                "\"fallback_step\":null," +
                "\"documents\":" +
                "[" +
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

    val polling_status_confirmed_response2 = "{\"id\":\"e154eaaf8639fb35ed1965acb9b18e1dcidt\",\"url\":\"https://solarisbank.payment.com/index.html?wizard_session_key=YUJg08KZHg4JI92n57vAjQRf6VAiLUwA8oojjxdP\\u0026interface_id=31de\",\"status\":\"confirmed\",\"failure_reason\":null,\"method\":\"bank\",\"authorization_expires_at\":\"2021-11-30T07:09:53.000Z\",\"confirmation_expires_at\":\"2021-11-30T07:08:22.000Z\",\"provider_status_code\":null,\"next_step\":null,\"fallback_step\":null,\"documents\":[{\"id\":\"4fe0aacda93938a902e1b7c5d6f6c92acdoc\",\"name\":\"e154eaaf8639fb35ed1965acb9b18e1dcidt_prepared_for_signing_kyc_report.pdf\",\"content_type\":\"application/pdf\",\"document_type\":\"QES_DOCUMENT\",\"size\":88471,\"customer_accessible\":false,\"created_at\":\"2021-11-30T06:59:24.000Z\"}],\"current_reference_token\":\"Transaktions-ID: 3195-8398\",\"reference\":null}"

    val dispatcher: Dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            println("request.path : ${request.path}")
            return when(request.path) {

                "/sign_documents/identificationId1234/confirm" -> {
                    //todo add logforj to tests
                    println("ConfirmContractSignUseCaseTest, dispatch")
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(confirmed200_method_fourthline_signing_response)
                    }
                }
                "/identifications/identificationId1234" -> {
                    println("ConfirmContractSignUseCaseTest, dispatch 2")
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(polling_status_confirmed_response2)
                    }
                }
                else -> {
                    println("ConfirmContractSignUseCaseTest, dispatch 3")
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(polling_status_confirmed_response)
                    }
                }
            }
        }
    }

    val mockWebServer = MockWebServer().apply {
        this.dispatcher = dispatcher
    }

    fun obtainConfirmContractSignUseCase(): ConfirmContractSignUseCase {

        mockkConstructor(QesStepParametersRepository::class)
        every { anyConstructed<QesStepParametersRepository>().getQesStepParameters() } returns
                QesStepParametersDto(true, false)

        mockkConstructor(IdentificationInMemoryDataSource::class)
        every { anyConstructed<IdentificationInMemoryDataSource>().obtainIdentificationDto() } returns
                Single.just(
                    mockk<IdentificationDto>() {
                        every { id } returns "identificationId1234"
                    }
                )
        every { anyConstructed<IdentificationInMemoryDataSource>().saveIdentification(any()) } returns
                Completable.complete()

        return IdentHubTestComponent
            .getTestInstance(
                networkModule = NetworkModuleTestFactory(mockWebServer).provideNetworkModule(),
            )
            .confirmContractSignUseCaseProvider
            .get()
    }

    "checkFourthlineSigningConfirmed" {
        val expectedContractSigningState =
            ContractSigningState.CONFIRMED("e154eaaf8639fb35ed1965acb9b18e1dcidt")
        var actualContractSigningStateResult : Result<ContractSigningState>? = null

        actualContractSigningStateResult =
            obtainConfirmContractSignUseCase().execute("123456").blockingGet()

        actualContractSigningStateResult.succeeded shouldBe true

        actualContractSigningStateResult.data shouldBe expectedContractSigningState
    }

})