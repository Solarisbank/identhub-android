package de.solarisbank.identhub.phone

import de.solarisbank.identhub.phone.feature.PhoneVerificationAction
import de.solarisbank.identhub.phone.feature.PhoneVerificationEvent
import de.solarisbank.identhub.phone.feature.PhoneVerificationUseCase
import de.solarisbank.identhub.phone.feature.PhoneVerificationViewModel
import de.solarisbank.sdk.data.dto.MobileNumberDto
import de.solarisbank.sdk.domain.model.result.Result
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Single

class PhoneVerificationViewModelTest: StringSpec({
    val testPhoneNumber = "+15551234"
    lateinit var viewModel: PhoneVerificationViewModel

    beforeAny {
        val phoneVerificationUseCase = mockk<PhoneVerificationUseCase>()

        every { phoneVerificationUseCase.fetchPhoneNumber() } returns Single.just(Result.Success(MobileNumberDto(testPhoneNumber)))
        every { phoneVerificationUseCase.authorize() } returns Completable.complete()
        every { phoneVerificationUseCase.verifyToken(any()) } returns Completable.complete()

        viewModel = PhoneVerificationViewModel(phoneVerificationUseCase)
    }

    "When timer expires resend button will be visible" {
        viewModel.onAction(PhoneVerificationAction.TimerExpired)
        viewModel.state().observeForever {
            it.shouldShowResend shouldBe true
        }
    }

    "When resend code is tapped resend button should go away" {
        viewModel.onAction(PhoneVerificationAction.TimerExpired)
        viewModel.onAction(PhoneVerificationAction.ResendCode)
        viewModel.state().observeForever {
            it.shouldShowResend shouldBe false
        }
    }

    "When resend code is tapped code should be resent" {
        viewModel.onAction(PhoneVerificationAction.ResendCode)
        viewModel.events().value?.content shouldBe PhoneVerificationEvent.CodeResent
    }

    "Phone number will be available" {
        viewModel.state().observeForever {
            it.phoneNumber shouldBe testPhoneNumber
        }
    }

    "When submit is tapped result will be available" {
        viewModel.onAction(PhoneVerificationAction.Submit("123456"))
        viewModel.state().value?.verifyResult shouldBe Result.Success(Unit)
    }

    "Submit button is disabled by default" {
        viewModel.state().value?.submitEnabled shouldBe false
    }

    "When code is short on characters submit button is disabled" {
        viewModel.onAction(PhoneVerificationAction.CodeChanged("123"))
        viewModel.state().value?.submitEnabled shouldBe false
    }

    "When code is long enough submit button will be enabled" {
        viewModel.onAction(PhoneVerificationAction.CodeChanged("123456"))
        viewModel.state().value?.submitEnabled shouldBe true
    }
})