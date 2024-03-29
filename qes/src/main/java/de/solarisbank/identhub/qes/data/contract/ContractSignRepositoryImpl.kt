package de.solarisbank.identhub.data.contract

import de.solarisbank.sdk.data.datasource.IdentificationLocalDataSource
import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

class ContractSignRepositoryImpl(
    private val contractSignNetworkDataSource: ContractSignNetworkDataSource,
    private val identificationLocalDataSource: IdentificationLocalDataSource
) : ContractSignRepository {
    override fun authorize(identificationId: String): Single<IdentificationDto> {
        return contractSignNetworkDataSource.postAuthorize(identificationId)
    }

    override fun confirmToken(
        identificationId: String,
        token: String
    ): Single<IdentificationDto> {
        return contractSignNetworkDataSource.postConfirm(
            identificationId,
            token
        )
    }

    override fun fetchDocumentFile(documentId: String): Single<Response<ResponseBody>> {
        return contractSignNetworkDataSource.fetchDocumentFile(documentId)
    }

    override fun deleteAll(): Completable {
        return identificationLocalDataSource.deleteIdentification()
    }

    override fun getIdentification(): Single<IdentificationDto> {
        return identificationLocalDataSource.obtainIdentificationDto()
    }

    override fun save(identificationDto: IdentificationDto): Completable {
        return identificationLocalDataSource.saveIdentification(identificationDto)
    }
}