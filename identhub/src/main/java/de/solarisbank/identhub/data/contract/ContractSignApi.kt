package de.solarisbank.identhub.data.contract

import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber
import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ContractSignApi {
    @PATCH("sign_documents/{identification_uid}/authorize")
    fun postAuthorize(
        @Path("identification_uid") identificationId: String
    ): Single<IdentificationDto>

    @PATCH("sign_documents/{identification_uid}/confirm")
    fun postConfirm(
        @Path("identification_uid") identificationId: String,
        @Body tan: TransactionAuthenticationNumber
    ): Single<IdentificationDto>

    @GET("sign_documents/{document_uid}/download")
    fun getDocumentFile(
        @Path("document_uid") documentId: String
    ): Single<Response<ResponseBody>>
}