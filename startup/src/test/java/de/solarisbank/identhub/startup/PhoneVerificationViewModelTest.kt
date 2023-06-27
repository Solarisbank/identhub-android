package de.solarisbank.identhub.startup

import de.solarisbank.identhub.startup.feature.PhoneVerificationAction
import de.solarisbank.identhub.startup.feature.PhoneVerificationEvent
import de.solarisbank.identhub.startup.feature.PhoneVerificationUseCase
import de.solarisbank.identhub.startup.feature.PhoneVerificationViewModel
import de.solarisbank.sdk.data.datasource.MobileNumberDataSourceImpl
import de.solarisbank.sdk.data.dto.MobileNumberDto
import de.solarisbank.sdk.data.utils.IdenthubDispatchers
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.usecase.MobileNumberUseCaseImpl
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class PhoneVerificationViewModelTest: StringSpec({
    val testPhoneNumber = "+15551234"
    val maskedPhoneNumber = "+****1234"
    lateinit var viewModel: PhoneVerificationViewModel

    beforeAny {
        val phoneVerificationUseCase = mockk<PhoneVerificationUseCase>()
        val mobileNumberDataSource = mockk<MobileNumberDataSourceImpl>()

        val mobileNumberUseCase = MobileNumberUseCaseImpl(mobileNumberDataSource)
        Dispatchers.setMain(Dispatchers.Default)
        val dispatchers = IdenthubDispatchers(Dispatchers.Default, Dispatchers.Default)

        coEvery { mobileNumberDataSource.getMobileNumber() } returns MobileNumberDto(testPhoneNumber)
        every { phoneVerificationUseCase.authorize() } returns Completable.complete()
        every { phoneVerificationUseCase.verifyToken(any()) } returns Completable.complete()

        viewModel = PhoneVerificationViewModel(phoneVerificationUseCase, mobileNumberUseCase, dispatchers)
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
            it.phoneNumber shouldBe maskedPhoneNumber
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