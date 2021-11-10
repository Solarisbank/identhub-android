package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.identhub.file.FileController
import de.solarisbank.sdk.data.dto.DocumentDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class FetchPdfUseCase(
    private val contractSignRepository: ContractSignRepository,
    private val fileController: FileController
) : SingleUseCase<DocumentDto, File?>() {
    override fun invoke(document: DocumentDto): Single<NavigationalResult<File?>> {
        return Single
            .concat(
                Single.just(NavigationalResult(fileController.retrieve(document.name))),
                contractSignRepository
                    .fetchDocumentFile(document.id)
                    .map { responseBody: Response<ResponseBody> ->
                        println("FetchPdfUseCase map $responseBody, ${responseBody.body()!!.source()}")
                        return@map NavigationalResult(
                            fileController.save(document.name, responseBody.body()!!.source())
                        )
                    }
            )
            .map { it ->
                println("FetchPdfUseCase2 $it")
                return@map it
            }
            .filter { result -> result.data != null }
            .first(NavigationalResult(null))
    }
}