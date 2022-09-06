package de.solarisbank.identhub.data.contract

import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

interface ContractSignRepository {
    fun authorize(identificationId: String): Single<IdentificationDto>
    fun confirmToken(identificationId: String, token: String): Single<IdentificationDto>
    fun deleteAll(): Completable
    fun fetchDocumentFile(documentId: String): Single<Response<ResponseBody>>
    fun getIdentification(): Single<IdentificationDto>
    fun save(identificationDto: IdentificationDto): Completable
}