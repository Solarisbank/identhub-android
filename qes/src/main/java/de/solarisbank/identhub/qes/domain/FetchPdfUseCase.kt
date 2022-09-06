package de.solarisbank.identhub.qes.domain

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.sdk.data.dto.DocumentDto
import de.solarisbank.sdk.data.entity.NavigationalResult
import de.solarisbank.sdk.domain.usecase.SingleUseCase
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
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
                        Timber.d("invoke 1, $responseBody, ${responseBody.body()!!.source()}")
                        return@map NavigationalResult(
                            fileController.save(document.name, responseBody.body()!!.source())
                        )
                    }
            )
            .filter { result: NavigationalResult<File?> -> result.data != null }
            .map { it: NavigationalResult<File?> ->
                Timber.d("invoke 2, it.data!!.path: ${it.data!!.path}")
                return@map it
            }
            .first(NavigationalResult(null))
    }
}