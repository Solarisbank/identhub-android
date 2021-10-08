package de.solarisbank.identhub.session.domain

import de.solarisbank.identhub.session.feature.di.IdentHubSessionTestComponent
import de.solarisbank.sdk.data.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.data.entity.NavigationalResult
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class IdentHubSessionUseCaseTest : StringSpec ({

    val initialization200Response =
        "{" +
                "\"first_step\":\"bank/iban\"," +
                "\"fallback_step\":\"fourthline/simplified\"," +
                "\"allowed_retries\":5," +
                "\"fourthline_provider\":\"SolarisBank\"," +
                "\"partner_settings\":{\"default_to_fallback_step\":true}" +
                "}"

    val dispatcher: Dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            println("dispatch 0 request.path : ${request.path}, request.body : ${request.body}")
            return when (request.path) {
                "/" -> {
                    MockResponse().apply {
                        setResponseCode(200)
                        setBody(initialization200Response)
                    }
                }
                else -> {
                    println("dispatch 4")
                    MockResponse().setResponseCode(404)
                }
            }
        }

    }

    val identHubSessionUseCase =
        IdentHubSessionTestComponent
            .getTestInstance(NetworkModuleTestFactory(dispatcher).provideNetworkModule())
            .identHubSessionUseCaseProvider
            .get()

    "initializationSuccessful200Response"{
        val identificationState: NavigationalResult<String> =
            identHubSessionUseCase.obtainLocalIdentificationState().blockingGet()

        identificationState.data shouldBe "FIRST_STEP_KEY"
        identificationState.nextStep shouldBe "bank/iban"
    }


})
