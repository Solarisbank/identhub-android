package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.Document
import de.solarisbank.sdk.data.entity.Identification
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

interface ContractSignRepository {
    fun authorize(identificationId: String): Single<IdentificationDto>
    fun confirmToken(identificationId: String, token: TransactionAuthenticationNumber): Single<IdentificationDto>
    fun deleteAll(): Completable
    fun fetchDocumentFile(documentId: String): Single<Response<ResponseBody>>
    fun getDocuments(): Single<List<Document>>
    fun getIdentification(): Single<Identification>
    fun save(identificationDto: IdentificationDto): Completable
}