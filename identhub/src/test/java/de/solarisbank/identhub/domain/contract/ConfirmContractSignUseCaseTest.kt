package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.contract.ContractSignApi
import de.solarisbank.identhub.data.contract.ContractSignNetworkDataSource
import de.solarisbank.identhub.data.contract.step.parameters.QesStepParametersRepository
import de.solarisbank.identhub.data.di.contract.ContractSignModule
import de.solarisbank.identhub.data.dto.QesStepParametersDto
import de.solarisbank.identhub.di.IdentHubTestComponent
import de.solarisbank.sdk.data.datasource.IdentificationInMemoryDataSource
import de.solarisbank.sdk.data.di.IdentificationModule
import de.solarisbank.sdk.data.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Factory
import io.kotest.core.spec.style.StringSpec
import io.mockk.*
import io.reactivex.Single
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import retrofit2.Retrofit

class ConfirmContractSignUseCaseTest : StringSpec({

    val confrim200reponse = "{" +
            "\"id\":\"6ad408f69edcbdea630d30950d354287cidt\"," +
            "\"reference\":null," +
            "\"url\":\"https://solarisbank.com/index.html?wizard_session_key=89wzh5W8qrAgD7ItRMpewEEwUnJTu2FWpZQE2NqY\\u0026interface_id=31de\"," +
            "\"status\":\"confirmed\"," +
            "\"completed_at\":null," +
            "\"method\":\"bank\"," +
            "\"proof_of_address_type\":null," +
            "\"proof_of_address_issued_at\":null," +
            "\"iban\":\"DE77110101010100000093\"," +
            "\"terms_and_conditions_signed_at\":\"2021-11-05T00:16:57.000Z\"," +
            "\"authorization_expires_at\":\"2021-11-05T00:27:35.000Z\"," +
            "\"confirmation_expires_at\":\"2021-11-05T00:26:41.000Z\"," +
            "\"current_reference_token\":\"Transaktions-ID: 7382-7878\"" +
            "}"

    val dispatcher: Dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {

            when(request.path) {

                "sign_documents/{identification_uid}/confirm" -> {
                    //todo add logforj to tests
                    println("ConfirmContractSignUseCaseTest, dispatch")
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(confrim200reponse)
                    }
                }

                else -> {

                }
            }

            return MockResponse()
        }
    }

    val identificationDtoMockk = mockk<IdentificationDto>(relaxed = true) {
        every { status }
    }

    val identificationDto = IdentificationDto(
        id = "",
        reference = "",
        url = "",
        status = "",
        failureReason = "null",
        method = null,
        nextStep = null,
        fallbackStep = null,
        completedAt = null,
        proofOfAddressType = null,
        proofOfAddressIssuedAt = null,
        termsAndConditionsSignedAt = null,
        authorizationExpiresAt = null,
        confirmationExpiresAt = null,
        estimatedWaitingTime = null,
        providerStatusCode = null,
        documents = listOf()
    )

//    mockkConstructor(IdentificationInMemoryDataSource::class)

    val identificationInMemoryDataSource = mockk<IdentificationInMemoryDataSource>() {
        every { obtainIdentificationDto() } returns Single.just(identificationDtoMockk)
    }

    lateinit var contractSignNetworkDataSourceSpyk: ContractSignNetworkDataSource
    var identificationInMemoryDataSourceSpyk: IdentificationInMemoryDataSource?
    var contractSignNetworkDataSourceSlot = slot<ContractSignNetworkDataSource>()
    val slotIdentificationInMemoryDataSource = slot<IdentificationInMemoryDataSource>()


    val contractSignModule = mockk<ContractSignModule>(relaxed = true) {

        val contractSignModuleReal = ContractSignModule()
        val contractSignApi = slot<ContractSignApi>()
        val retrofitSlot = slot<Retrofit>()


        every { provideContractSignApi(capture(retrofitSlot)) } answers {
            contractSignModuleReal.provideContractSignApi(retrofitSlot.captured)
        }

        every {
            provideContractSignNetworkDataSource(capture(contractSignApi))
        } answers {
            Factory {
                spyk(
                    contractSignModuleReal
                        .provideContractSignNetworkDataSource(contractSignApi.captured)
                        .get()
                ).also { contractSignNetworkDataSourceSpyk = it }
            }
        }

        every {
            provideContractSignRepository(
                capture(contractSignNetworkDataSourceSlot),
                capture(slotIdentificationInMemoryDataSource)
            )
        } answers {
            Factory {
                contractSignModuleReal.provideContractSignRepository(
                    contractSignNetworkDataSourceSlot.captured,
                    identificationInMemoryDataSource
                ).get()
            }
        }
    }



    val identificationModule = mockk<IdentificationModule>(relaxed = true) {
        every { provideIdentificationLocalDataSource() } returns
                DoubleCheck.provider(object : Factory<IdentificationInMemoryDataSource> {
                    override fun get(): IdentificationInMemoryDataSource {
                        return identificationInMemoryDataSource
                    }

                })
    }

    val mockWebServer = MockWebServer().apply {
        this.dispatcher = dispatcher
    }


    var identHubTestComponent: IdentHubTestComponent? = null

    beforeSpec {
        mockkConstructor(QesStepParametersRepository::class)

        every { anyConstructed<QesStepParametersRepository>().getQesStepParameters() } returns
                QesStepParametersDto(
                    false, true
                )

        identHubTestComponent = IdentHubTestComponent
            .getTestInstance(
                networkModule = NetworkModuleTestFactory(mockWebServer).provideNetworkModule(),
                contractSignModule = contractSignModule,
                identificationModule = identificationModule
            )
    }


    //todo extend test cases
    "CheckObtainedConfirmedIdentificationId" {
        identHubTestComponent!!.confirmContractSignUseCaseProvider
            .get().execute("123456")
//        verify {  }
        verify { identificationInMemoryDataSource.obtainIdentificationDto() }
    }

})