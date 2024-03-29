package de.solarisbank.identhub.data.contract

import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

interface ContractSignNetworkDataSource {

    fun postAuthorize(identificationId: String): Single<IdentificationDto>

    fun postConfirm(
        identificationId: String,
        tan: String
    ): Single<IdentificationDto>

    fun fetchDocumentFile(documentId: String): Single<Response<ResponseBody>>
}