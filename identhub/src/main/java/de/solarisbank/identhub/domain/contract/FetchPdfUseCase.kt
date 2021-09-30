package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.file.FileController
import de.solarisbank.sdk.data.dto.DocumentDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import de.solarisbank.sdk.feature.Optional
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class FetchPdfUseCase(
        private val contractSignRepository: ContractSignRepository,
        private val fileController: FileController
) : SingleUseCase<DocumentDto, Optional<File>>() {
    override fun invoke(document: DocumentDto): Single<NavigationalResult<Optional<File>>> {
        return Single.concat(Single.just(fileController[document.name]),
                contractSignRepository.fetchDocumentFile(document.id)
                        .map { responseBody: Response<ResponseBody> -> fileController.save(document.name, responseBody.body()!!.source()) })
                .filter { obj: Optional<File> -> obj.isPresent }
                .first(Optional.empty())
                .map { NavigationalResult(it) }
    }
}