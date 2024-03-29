package de.solarisbank.identhub.qes

import de.solarisbank.identhub.qes.contract.sign.ContractSigningAction
import de.solarisbank.identhub.qes.contract.sign.ContractSigningEvent
import de.solarisbank.identhub.qes.contract.sign.ContractSigningViewModel
import de.solarisbank.identhub.qes.data.dto.ContractSigningResult
import de.solarisbank.identhub.qes.domain.AuthorizeContractSignUseCase
import de.solarisbank.identhub.qes.domain.ConfirmContractSignUseCase
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
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ContractSigningViewModelTest: StringSpec({
    val testPhoneNumber = "+15551234"
    val maskedPhoneNumber = "+****1234"
    val testIdentId = "identId"
    val testSigningResult = Result.Success(ContractSigningResult.Confirmed(testIdentId), null)
    lateinit var viewModel: ContractSigningViewModel

    beforeAny {
        val authorizeUseCase = mockk<AuthorizeContractSignUseCase>()
        val confirmUseCase = mockk<ConfirmContractSignUseCase>()
        val mobileNumberDataSource = mockk<MobileNumberDataSourceImpl>()
        val mobileNumberUseCase = MobileNumberUseCaseImpl(mobileNumberDataSource)
        Dispatchers.setMain(Dispatchers.Default)
        val dispatchers = IdenthubDispatchers(Dispatchers.Default, Dispatchers.Default)

        every { authorizeUseCase.execute(Unit) } returns Completable.complete()
        every { confirmUseCase.execute(any()) } returns Single.just(testSigningResult)
        coEvery { mobileNumberDataSource.getMobileNumber() } returns MobileNumberDto(testPhoneNumber)
        viewModel = ContractSigningViewModel(authorizeUseCase, confirmUseCase, mobileNumberUseCase, dispatchers)
    }

    "When timer expires resend button will be visible" {
        viewModel.onAction(ContractSigningAction.TimerExpired)
        viewModel.state().observeForever {
            it.shouldShowResend shouldBe true
        }
    }

    "When resend code is tapped resend button should go away" {
        viewModel.onAction(ContractSigningAction.TimerExpired)
        viewModel.onAction(ContractSigningAction.ResendCode)
        viewModel.state().observeForever {
            it.shouldShowResend shouldBe false
        }
    }

    "When resend code is tapped code should be resent" {
        viewModel.onAction(ContractSigningAction.ResendCode)
        viewModel.events().value?.content shouldBe ContractSigningEvent.CodeResent
    }

    "Phone number will be available" {
        viewModel.state().observeForever {
            it.phoneNumber shouldBe maskedPhoneNumber
        }
    }

    "When submit is tapped result will be available" {
        viewModel.onAction(ContractSigningAction.Submit("123456"))
        viewModel.state().value?.signingResult shouldBe testSigningResult
    }
})