package de.solarisbank.identhub.data.contract

import de.solarisbank.identhub.data.Mapper
import de.solarisbank.identhub.data.TransactionAuthenticationNumber
import de.solarisbank.identhub.domain.contract.ContractSignRepository
import de.solarisbank.identhub.domain.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.Document
import de.solarisbank.sdk.data.entity.Identification
import de.solarisbank.sdk.data.entity.IdentificationWithDocument
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

class ContractSignDataSourceRepository(
        private val contractSignNetworkDataSource: ContractSignNetworkDataSource,
        private val contractSignLocalDataSource: ContractSignLocalDataSource,
        private val identificationEntityMapper: Mapper<IdentificationDto, IdentificationWithDocument>
) : ContractSignRepository {
    override fun authorize(identificationId: String): Single<IdentificationDto> {
        return contractSignNetworkDataSource.postAuthorize(identificationId)
    }

    override fun confirmToken(identificationId: String, token: TransactionAuthenticationNumber): Single<IdentificationDto> {
        return contractSignNetworkDataSource.postConfirm(identificationId, token)
    }

    override fun fetchDocumentFile(documentId: String): Single<Response<ResponseBody>> {
        return contractSignNetworkDataSource.fetchDocumentFile(documentId)
    }

    override fun getDocuments(): Single<List<Document>> {
        return contractSignLocalDataSource.getAllDocuments()
    }

    override fun deleteAll(): Completable {
        return contractSignLocalDataSource.deleteAll()
    }

    override fun getIdentification(): Single<Identification> {
        return contractSignLocalDataSource.getIdentification()
    }

    override fun save(identificationDto: IdentificationDto): Completable {
        val identificationWithDocument = identificationEntityMapper.to(identificationDto)
        return contractSignLocalDataSource.insert(identificationWithDocument)
    }
}