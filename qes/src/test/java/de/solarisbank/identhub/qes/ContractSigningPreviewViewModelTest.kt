package de.solarisbank.identhub.qes

import de.solarisbank.identhub.qes.contract.preview.ContractSigningPreviewAction
import de.solarisbank.identhub.qes.contract.preview.ContractSigningPreviewEvent
import de.solarisbank.identhub.qes.contract.preview.ContractSigningPreviewViewModel
import de.solarisbank.identhub.qes.domain.FetchPdfUseCase
import de.solarisbank.identhub.qes.domain.GetDocumentsUseCase
import de.solarisbank.identhub.qes.domain.GetIdentificationUseCase
import de.solarisbank.sdk.data.dto.DocumentDto
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.initial.IdenthubInitialConfig
import de.solarisbank.sdk.domain.model.result.Result
import de.solarisbank.sdk.domain.model.result.data
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import java.io.File

class ContractSigningPreviewViewModelTest: StringSpec({
    val testDocument = DocumentDto(
        "docId",
        "docName",
        "pdf",
        "pdf",
        100,
        true
    )
    val testDownloadedDocumentResult: Result<File?> = Result.Success(null)
    lateinit var viewModel: ContractSigningPreviewViewModel

    beforeAny {
        val getDocumentUseCase = mockk<GetDocumentsUseCase>()
        val fetchPdfUseCase = mockk<FetchPdfUseCase>()
        val getIdentificationUseCase = mockk<GetIdentificationUseCase>()

        every { getDocumentUseCase.execute(Unit) } returns
                Single.just(Result.Success(listOf(testDocument)))
        every { getIdentificationUseCase.getInitialConfig() } returns
                IdenthubInitialConfig(
                        isTermsPreAccepted = true,
                        isPhoneNumberVerified = true,
                        isRemoteLoggingEnabled = false,
                        firstStep = "",
                        defaultFallbackStep = "",
                        allowedRetries = 1,
                        fourthlineProvider = "",
                        partnerSettings = null,
                        style = null
                )
        every { getIdentificationUseCase.execute(Unit) } returns
                Single.just(
                    Result.Success(
                        IdentificationDto(
                            "identId",
                            url = null,
                            status = "auth",
                            documents = listOf(testDocument)
                        )
                    )
                )
        every { fetchPdfUseCase.execute(any()) } returns
                Single.just(testDownloadedDocumentResult)
        viewModel = ContractSigningPreviewViewModel(
            getDocumentUseCase,
            fetchPdfUseCase,
            getIdentificationUseCase
        )
    }

    "State includes the QES documents" {
        viewModel.state().observeForever {
            it.documents.data.shouldContainExactly(testDocument)
        }
    }

    "Download document action" {
        viewModel.onAction(ContractSigningPreviewAction.DownloadDocument(testDocument))
        viewModel.events().value?.content shouldBe
                ContractSigningPreviewEvent.DocumentDownloaded(testDownloadedDocumentResult)
    }

    "Hitting Next button will result in a Done result" {
        viewModel.onAction(ContractSigningPreviewAction.Next)
        viewModel.events().value?.content shouldBe ContractSigningPreviewEvent.Done
    }
})

