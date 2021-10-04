package de.solarisbank.identhub.domain.verification.bank

import de.solarisbank.identhub.di.IdentHubTestComponent
import de.solarisbank.identhub.domain.data.dto.IbanVerificationModel
import de.solarisbank.sdk.data.di.network.NetworkModuleTestFactory
import de.solarisbank.sdk.domain.model.result.Result
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import io.kotest.matchers.types.shouldBeInstanceOf
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class VerifyIBanUseCaseTest : StringSpec({

    val IbanJointAccountNumber = "DE00000000000000000004"
    val IbanTrueNumber = "DE00000000000000000093"
    val IbanInvalidNumber = "DE00000000000000000022"
    val IbanJointAccount200Request = "{\"iban\":\"$IbanJointAccountNumber\"}"
    val IbanJointAccount200Response =
        "{" +
            "\"id\":\"42c5fb2dd03b3c1e74e876e3e3eb0feacidt\"," +
            "\"reference\":null," +
            "\"url\":\"https://solarisbank.de" +
                "/index.html?wizard_session_key=a4cEiyJ0Z4nitZW90LtEqV8eNGIvNQaS4yAKBBtE\\u0026interface_id=31de\"," +
            "\"status\":\"pending\"," +
            "\"completed_at\":null,\"method\":\"bank\"," +
            "\"proof_of_address_type\":null," +
            "\"proof_of_address_issued_at\":null," +
            "\"iban\":\"DE00000000000000000004\"," +
            "\"terms_and_conditions_signed_at\":\"2021-09-29T17:30:36.000Z\"," +
            "\"authorization_expires_at\":null,\"confirmation_expires_at\":null,\"estimated_waiting_time\":null" +
        "}"

    val Iban200Response =
        "{" +
            "\"id\":\"05c82a36cb2718e19fecb7b590d4ae0ecidt\"," +
            "\"reference\":null," +
            "\"url\":\"https://solarisbank.de" +
                "/index.html?wizard_session_key=CVPZGMeVhQNPBLvEy5W5CHLzKe9q0oWnivede0y5\u0026interface_id=31de\","+
            "\"status\":\"pending\"," +
            "\"completed_at\":null," +
            "\"method\":\"bank\"," +
            "\"proof_of_address_type\":null," +
            "\"proof_of_address_issued_at\":null," +
            "\"iban\":\"DE00000000000000000093\"," +
            "\"terms_and_conditions_signed_at\":\"2021-10-01T09:53:08.000Z\"," +
            "\"authorization_expires_at\":null," +
            "\"confirmation_expires_at\":null," +
            "\"estimated_waiting_time\":null" +
        "}"

    val Iban400Response =
        "{" +
            "\"errors\":[{" +
                "\"id\":\"35a698ba-f3e9-4172-9834-1244f3ec3a39\"," +
                "\"status\":400," +
                "\"code\":\"invalid_iban\"," +
                "\"title\":\"Invalid IBAN\"," +
                "\"detail\":\"IBAN ("+IbanInvalidNumber+") is structurally invalid\"" +
            "}]," +
            "\"backtrace\":[" +
                "\"IBAN ("+IbanInvalidNumber+") is structurally invalid\"," +
                "\"/app/lib/iban_bic_tools/iban/iban.rb:12:in `rescue in new'\"," +
                "\"/app/lib/iban_bic_tools/iban/iban.rb:7:in `new'\",\"/app/app/services/iban.rb:42:in `lookup'\"," +
                "\"/app/app/services/concerns/authorize_actions.rb:111:in `call'\"," +
                "\"/app/app/services/concerns/authorize_actions.rb:111:in `block in authorize_action'\"," +
                "\"/app/app/api/v1/iban/lookup.rb:21:in `block in \u003cclass:Lookup\u003e'\"," +
                "\"/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:59:in `call'\"," +
                "\"/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:59:in `block (2 levels) in generate_api_method'\"," +
                "\"/usr/local/bundle/gems/activesupport-5.2.6/lib/active_support/notifications.rb:170:in `instrument'\"," +
                "\"/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:58:in `block in generate_api_method'\"," +
                "\"/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:341:in `execute'\"," +
                "\"/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:267:in `block in run'\"," +
                "\"/usr/local/bundle/gems/activesupport-5.2.6/lib/active_support/notifications.rb:170:in `instrument'\"," +
                "\"/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:247:in `run'\"," +
                "\"/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:322:in `block in build_stack'\"," +
                "\"/usr/local/bundle/gems/grape-1.5.0/lib/grape/middleware/base.rb:36:in `call!'\"," +
                "\"/usr/local/bundle/gems/grape-1.5.0/lib/grape/middleware/base.rb:29:in `call'\"," +
                "\"/usr/local/bundle/gems/solaris-auth-0.4.3/lib/solaris/middleware/auth_context.rb:21:in `call'\"," +
                "\"/usr/local/bundle/gems/grape-1.5.0/lib/grape/middleware/error.rb:39:in `block in call!'\"," +
                "\"/usr/local/bundle/gems/grape-1.5.0/lib/grape/middleware/error.rb:38:in `catch'\"" +
            "]}"

    val dispatcher: Dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            println("dispatch 0 request.path : ${request.path}, request.body : ${request.body}")
            return when (request.path) {
                "/iban/verify" -> {
                    return if (request.body.toString().contains(IbanJointAccount200Request)) {
                        println("dispatch 1")
                        MockResponse().apply {
                            setResponseCode(200)
                            setBody(IbanJointAccount200Response)
                        }
                    }else if(request.body.toString().contains(IbanInvalidNumber)) {
                        println("dispatch 2")
                        MockResponse().apply {
                            setResponseCode(400)
                            setBody(Iban400Response)
                        }
                    } else {
                        println("dispatch 3")
                        MockResponse().setResponseCode(404)
                    }
                }
                else -> {
                    println("dispatch 4")
                    MockResponse().setResponseCode(404)
                }
            }
        }

    }

    val verifyIBanUseCase = IdentHubTestComponent
        .getTestInstance(NetworkModuleTestFactory(dispatcher).provideNetworkModule())
        .verifyIBanUseCaseProvider
        .get()

    "IbanJointAccount200" {
        val answer: Result<IbanVerificationModel> = verifyIBanUseCase.execute(IbanJointAccountNumber).blockingGet()

        answer.shouldBeInstanceOf<Result.Success<IbanVerificationModel>>()
        answer.data.shouldBeInstanceOf<IbanVerificationModel.IbanVerificationSuccessful>()
        val ibanVerificationSuccessful = ((answer as Result.Success<IbanVerificationModel>).data
                as IbanVerificationModel.IbanVerificationSuccessful)
        ibanVerificationSuccessful.bankIdentificationUrl should
                startWith(
                    "https://solarisbank.de/index.html?wizard_session_key=a4cEiyJ0Z4ni"
                )
        ibanVerificationSuccessful.nextStep shouldBe null

    }

    "IbanInvalid400" {

        val answer: Result<IbanVerificationModel> = verifyIBanUseCase.execute(IbanInvalidNumber).blockingGet()

        answer.shouldBeInstanceOf<Result.Success<IbanVerificationModel>>()
        answer.data.shouldBeInstanceOf<IbanVerificationModel.InvalidBankIdError>()

        val ibanVerificationSuccessful = ((answer as Result.Success<IbanVerificationModel>).data
                as IbanVerificationModel.InvalidBankIdError)
        ibanVerificationSuccessful.retryAllowed shouldBe true
    }
})