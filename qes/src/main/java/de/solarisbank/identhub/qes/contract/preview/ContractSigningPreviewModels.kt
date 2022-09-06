package de.solarisbank.identhub.qes.contract.preview

import de.solarisbank.sdk.data.dto.DocumentDto
import de.solarisbank.sdk.domain.model.result.Result
import java.io.File

data class ContractSigningPreviewState(
    val documents: Result<List<DocumentDto>>,
    val shouldShowTerms: Boolean
)

sealed class ContractSigningPreviewEvent {
    data class DocumentDownloaded(val result: Result<File?>): ContractSigningPreviewEvent()
    object Done: ContractSigningPreviewEvent()
}

sealed class ContractSigningPreviewAction {
    data class DownloadDocument(val document: DocumentDto) : ContractSigningPreviewAction()
    object Next: ContractSigningPreviewAction()
}