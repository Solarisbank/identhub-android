package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.TransactionAuthenticationNumber
import de.solarisbank.identhub.data.entity.Document
import de.solarisbank.identhub.data.entity.Identification
import de.solarisbank.identhub.domain.data.dto.IdentificationDto
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